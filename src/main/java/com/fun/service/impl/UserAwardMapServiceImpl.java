package com.fun.service.impl;

import com.fun.dao.UserAwardMapMapper;
import com.fun.dao.UserShopMapMapper;
import com.fun.dto.userAwardMap.UserAwardMapExecution;
import com.fun.entity.UserAwardMap;
import com.fun.entity.UserShopMap;
import com.fun.enums.UserAwardMapStateEnum;
import com.fun.exceptions.UserAwardMapOperationException;
import com.fun.service.UserAwardMapService;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 12:39:12
 * @Desc: 用户领取奖品映射业务层实现
 */
@Service
public class UserAwardMapServiceImpl implements UserAwardMapService {

    @Autowired
    private UserAwardMapMapper userAwardMapMapper;
    @Autowired
    private UserShopMapMapper userShopMapMapper;

    /**
     * 根据查询条件获取用户领取奖品的信息列表
     *
     * @param userAwardMap
     * @return
     */
    @Override
    public List<UserAwardMap> getUserAwardMapList(UserAwardMap userAwardMap) {
        if (userAwardMap != null) {
            return userAwardMapMapper.selectUserAwardList(userAwardMap);
        } else {
            return null;
        }
    }

    /**
     * 通过主键获取用户领取奖品的信息
     *
     * @param userAwardMapId
     * @return
     */
    @Override
    public UserAwardMap getUserAwardMapById(Integer userAwardMapId) {
        if (userAwardMapId > 0) {
            return userAwardMapMapper.selectUserAwardById(userAwardMapId);
        }
        return null;
    }

    /**
     * 领取奖品,添加用户领取奖品的映射信息
     *
     * @param userAwardMapCondition
     * @return
     */
    @Override
    @Transactional
    public UserAwardMapExecution addUserAwardMap(UserAwardMap userAwardMapCondition) {
        if (userAwardMapCondition != null && userAwardMapCondition.getUser() != null
                && userAwardMapCondition.getUser().getUserId() > 0 && userAwardMapCondition.getShop() != null
                && userAwardMapCondition.getShop().getShopId() > 0) {
            //添加默认值,未领取
            userAwardMapCondition.setCreateTime(new Date());
            userAwardMapCondition.setUsedStatus(0);
            try {
                int effectNum = 0;
                //如果该奖品兑换需要积分,则扣除对应兑换用户的积分(UserShopMap下商店对应的用户拥有的积分)
                if (userAwardMapCondition.getPoint() != null && userAwardMapCondition.getPoint() > 0) {
                    //通过用户Id和商店Id获取该用户在该商店积分
                    UserShopMap userShopMap = userShopMapMapper.selectUserShopMapById(userAwardMapCondition.getUser().getUserId(), userAwardMapCondition.getShop().getShopId());
                    //判断该用户在该商店是否有积分
                    if (userShopMap != null) {

                        //积分扣除
                        userShopMap.setPoint(userShopMap.getPoint() - userAwardMapCondition.getPoint());
                        //更新积分信息
                        effectNum = userShopMapMapper.updateUserShopMapPoint(userShopMap);
                        if (effectNum <= 0) {
                            throw new UserAwardMapOperationException("更新积分信息失败");
                        }
                    } else {
                        throw new UserAwardMapOperationException("积分不足");
                    }
                } else {
                    throw new UserAwardMapOperationException("在本店没有积分");
                }
                //插入奖品兑换信息
                effectNum = userAwardMapMapper.insertUserAwardMap(userAwardMapCondition);
                if (effectNum <= 0) {
                    throw new UserAwardMapOperationException("领取奖品失败");
                }
                return new UserAwardMapExecution(UserAwardMapStateEnum.SUCCESS);
            } catch (Exception e) {
                throw new UserAwardMapOperationException("insertUserAwardMap error:" + e.toString());
            }
        } else {
            return new UserAwardMapExecution(UserAwardMapStateEnum.NULL_USERAWARD_INFO);
        }
    }

    /**
     * 修改领取奖品的映射信息(主要修改奖品领取状态)
     *
     * @param userAwardMapCondition
     * @return
     */
    @Override
    @Transactional
    public UserAwardMapExecution updateUserAwardMap(UserAwardMap userAwardMapCondition) {
        if (userAwardMapCondition == null || userAwardMapCondition.getUserAwardId() == null ||
                userAwardMapCondition.getUsedStatus() == null) {
            return new UserAwardMapExecution(UserAwardMapStateEnum.NULL_USERAWARD_INFO);
        } else {
            try {
                //更新可用状态
                int effectNum = userAwardMapMapper.updateUserAwardMap(userAwardMapCondition);
                if (effectNum <= 0) {
                    return new UserAwardMapExecution(UserAwardMapStateEnum.INNER_ERROR);
                } else {
                    return new UserAwardMapExecution(UserAwardMapStateEnum.SUCCESS, userAwardMapCondition);
                }
            } catch (Exception e) {
                throw new UserAwardMapOperationException("updateUserAwardMap error:" + e.toString());
            }
        }
    }
}
