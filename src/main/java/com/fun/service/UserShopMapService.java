package com.fun.service;

import com.fun.entity.UserShopMap;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 11:49:22
 * @Desc: 顾客和商店积分映射接口
 */
public interface UserShopMapService {

    /**
     * 根据查询条件获取用户积分信息列表
     *
     * @param userShopMapCondition
     * @return
     */
    List<UserShopMap> getUserShopMapList(UserShopMap userShopMapCondition);

    /**
     * 通过用户id和商店id查询该用户在某商店的积分信息
     *
     * @param userId
     * @param shopId
     * @return
     */
    UserShopMap getUserShopMapById(Integer userId, Integer shopId);
}
