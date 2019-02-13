package com.fun.dao;

import com.fun.entity.UserProductMap;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-12 22:30:34
 * @Desc: 顾客消费商品的映射dao
 */
public interface UserProductMapMapper {

    /**
     * 根据查询条件(顾客id,商店id,顾客名,商品名,消费日期)查询用户消费商品映射的列表
     *
     * @param userProductMap
     * @return
     */
    List<UserProductMap> selectUserProductMapList(UserProductMap userProductMap);

    /**
     * 添加一条顾客购买商品的记录
     *
     * @param userProductMap
     * @return
     */
    int insertUserProductMap(UserProductMap userProductMap);
}