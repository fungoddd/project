package com.fun.service.impl;

import com.fun.dao.UserShopMapMapper;
import com.fun.entity.UserShopMap;
import com.fun.service.UserShopMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 11:54:36
 * @Desc: 顾客和商店积分映射业务层实现
 */
@Service
public class UserShopMapServiceImpl implements UserShopMapService {

    @Autowired
    private UserShopMapMapper userShopMapMapper;

    /**
     * 根据查询条件获取用户积分信息列表
     *
     * @param userShopMapCondition
     * @return
     */
    @Override
    public List<UserShopMap> getUserShopMapList(UserShopMap userShopMapCondition) {
        if (userShopMapCondition != null) {
            return userShopMapMapper.selectUserShopMapList(userShopMapCondition);
        } else {
            return null;
        }
    }

    /**
     * 通过用户id和商店id查询该用户在某商店的积分信息
     *
     * @param userId
     * @param shopId
     * @return
     */
    @Override
    public UserShopMap getUserShopMapById(Integer userId, Integer shopId) {
        if (userId > 0 && shopId > 0) {
            return userShopMapMapper.selectUserShopMapById(userId, shopId);
        } else {
            return null;
        }
    }
}
