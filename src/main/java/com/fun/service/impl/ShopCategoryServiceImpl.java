package com.fun.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.dao.ShopCategoryMapper;
import com.fun.entity.ShopCategory;
import com.fun.exceptions.ShopCategoryOperationException;
import com.fun.service.ShopCategoryService;
import com.fun.util.cache.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-02 01:28:11
 * @Desc: 商品类别操作业务层
 */
@Service
public class ShopCategoryServiceImpl implements ShopCategoryService {

    @Autowired
    private ShopCategoryMapper shopCategoryMapper;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;


    /**
     * 根据查询条件获取商品类别列表
     *
     * @param shopCategoryCondition
     * @return List<ShopCategory>
     */
    @Override
    public List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition) {
        //定义redis中商品类别的key
        String key = SHOP_CATEGORY_LIST_KEY;

        List<ShopCategory> shopCategoryList = null;

        ObjectMapper objectMapper = new ObjectMapper();
        //拼接redis的key
        if (shopCategoryCondition == null) {
            //查询条件为空取出所有的首页分类(且parentId为null的显示在首页的商品类别)
            key = key + "_all-Level-1";

        } else if (shopCategoryCondition != null && shopCategoryCondition.getParentId() != null) {
            //如果parentId不为空,取出parentId下的所有子类(也就是头条显示的大类下的子类别)
            key = key + "_parent" + shopCategoryCondition.getParentId();
        } else if (shopCategoryCondition != null) {
            //如果只是商品类别不为空作为查询条件,取出所有子类别
            // (也就是点击全部商店:所有parentId不为空的二级类别,不管是哪个大类下)
            key = key + "_all-Level-2";
        }

        //判断key是否在redis中存在
        if (!jedisKeys.exists(key)) {
            //不存在从数据库中查
            shopCategoryList = shopCategoryMapper.selectShopCategory(shopCategoryCondition);

            String jsonString;
            try {
                jsonString = objectMapper.writeValueAsString(shopCategoryList);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new ShopCategoryOperationException(e.toString());
            }
            //存到redis中
            jedisStrings.set(key, jsonString);

        } else {
            String jsonString = jedisStrings.get(key);
            //将String类型转为list
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, ShopCategory.class);

            try {
                shopCategoryList = objectMapper.readValue(jsonString, javaType);

            } catch (IOException e) {
                e.printStackTrace();
                throw new ShopCategoryOperationException(e.toString());
            }

        }
        return shopCategoryList;
    }
}
