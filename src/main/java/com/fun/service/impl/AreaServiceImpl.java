package com.fun.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.dao.AreaMapper;
import com.fun.entity.Area;
import com.fun.exceptions.AreaOperationException;
import com.fun.service.AreaService;
import com.fun.util.cache.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-02 01:28:39
 * @Desc: 查询区域业务层
 */
@Service
public class AreaServiceImpl implements AreaService {

    //把需要到的对象注入进来
    @Autowired
    private AreaMapper areaMapper;
    //区域不常更新,所以引入缓存
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;

    private static Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);

    //查询区域列表
    @Override
    public List<Area> getAreaList() {
        //定义redis的key
        String key = AREA_LIST_KEY;
        //定义接收对象
        List<Area> areaList = null;
        //定义json数据转换操作类
        ObjectMapper objectMapper = new ObjectMapper();
        //判断redis中是否存在这个key
        if (!jedisKeys.exists(key)) {
            //如果不存在,从数据库中取出对应数据
            areaList = areaMapper.queryArea();

            String jsonString;
            try {
                //将相关实体类集合转为string,存入redis对应的key中
                jsonString = objectMapper.writeValueAsString(areaList);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.toString());

            }
            //设置到redis的key中
            jedisStrings.set(key, jsonString);

        } else {
            //如果存在,获取redis中的key
            String jsonString = jedisStrings.get(key);
            //把key对应的list字符串转为实体类对象
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Area.class);
            try {
                //areaList接收
                areaList = objectMapper.readValue(jsonString, javaType);

            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.toString());
            }
        }
        //返回查到的areaList
        return areaList;
    }
}
