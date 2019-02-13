package com.fun.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.dao.HeadLineMapper;
import com.fun.entity.HeadLine;
import com.fun.exceptions.HeadLineOperationException;
import com.fun.service.HeadLineService;
import com.fun.util.cache.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Authro: FunGod
 * @Date: 2018-12-02 19:41:15
 * @Desc: 显示头条列表业务层
 */
@Service
public class HeadLineServiceImpl implements HeadLineService {

    @Autowired
    private HeadLineMapper headLineMapper;

    @Autowired //redis中的key
    private JedisUtil.Keys jedisKeys;

    @Autowired//对应的Map
    private JedisUtil.Strings jedisStrings;



    /**
     * 查询头条列表
     *
     * @param headLine
     * @return List<HeadLine>
     */
    @Override
    @Transactional
    public List<HeadLine> getHeadLineList(HeadLine headLine) {
        //定义redis的前缀
        String key = HEADLINE_LIST_KEY;
        //接收对象
        List<HeadLine> headLineList = null;
        //定义json数据转换操作类
        ObjectMapper objectMapper = new ObjectMapper();

        //如果入参头条对象不为空并且状态不为空(方便管理员使用),三种不同的key存入redis
        if (headLine != null && headLine.getEnableStatus() != null) {
            //拼接key和其状态作为key,取出的是(禁用的头条或可用的头条),如果状态为空取出的使所有头条
            key = key + "_" + headLine.getEnableStatus();
        }

        //判断redis中key是否存在
        if (!jedisKeys.exists(key)) {
            //不存在从数据库查询取到
            headLineList = headLineMapper.selectHeadLine(headLine);
            String jsonString;
            try {
                //把查到的headList转为String
                jsonString = objectMapper.writeValueAsString(headLineList);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new HeadLineOperationException(e.toString());
            }
            //把key和对应转换得到jsonString(value)set到redis中,下次可以使用
            jedisStrings.set(key, jsonString);
        } else {
            //如果redis有这个key,直接从redis中取
            String jsonString = jedisStrings.get(key);
            //把得到的key对象的value对应的String类型的list转回实体类对象
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, HeadLine.class);

            try {
                //headLineList接收
                headLineList = objectMapper.readValue(jsonString, javaType);

            } catch (Exception e) {
                e.printStackTrace();
                throw new HeadLineOperationException(e.toString());
            }
        }
        return headLineList;
    }
}
