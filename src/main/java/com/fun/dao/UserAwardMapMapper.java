package com.fun.dao;

import com.fun.entity.UserAwardMap;


import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-12 20:33:14
 * @Desc: 顾客兑换奖品记录的dao
 */
public interface UserAwardMapMapper {
    /**
     * 根据传入查询条件获取:用户兑换奖品记录的列表(顾客id,商店id,顾客名,奖品可用状态查询)
     * @param userAwardMap
     * @return
     */
    List<UserAwardMap>selectUserAwardList(UserAwardMap userAwardMap);

    /**
     * 通过主键查询指定的奖品兑换记录
     * @param userAwardId
     * @return
     */
    UserAwardMap selectUserAwardById(Integer userAwardId);

    /**
     * 新增奖品兑换信息
     * @param userAwardMap
     * @return
     */
    int insertUserAwardMap(UserAwardMap userAwardMap);

    /**
     * 更新兑换奖品信息通过主键和用户id(更新奖品领取状态)
     * @param userAwardMap
     * @return
     */
    int updateUserAwardMap(UserAwardMap userAwardMap);

    /**
     * 删除奖品兑换信息
     * @param userAwardId
     * @return
     */
    int deleteUserAwardMap(Integer userAwardId);
}