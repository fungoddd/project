package com.fun.service;

import com.fun.dto.userAwardMap.UserAwardMapExecution;
import com.fun.entity.UserAwardMap;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 12:36:51
 * @Desc: 用户领取奖品映射接口
 */
public interface UserAwardMapService {
    /**
     * 根据查询条件获取用户领取奖品的信息列表
     *
     * @param userAwardMap
     * @return
     */
    List<UserAwardMap> getUserAwardMapList(UserAwardMap userAwardMap);

    /**
     * 通过主键获取用户领取奖品的信息
     *
     * @param userAwardMapId
     * @return
     */
    UserAwardMap getUserAwardMapById(Integer userAwardMapId);

    /**
     * 领取奖品,添加用户领取奖品的映射信息
     *
     * @param userAwardMapCondition
     * @return
     */
    UserAwardMapExecution addUserAwardMap(UserAwardMap userAwardMapCondition);

    /**
     * 修改领取奖品的映射信息(主要修改奖品领取状态)
     *
     * @param userAwardMapCondition
     * @return
     */
    UserAwardMapExecution updateUserAwardMap(UserAwardMap userAwardMapCondition);

}
