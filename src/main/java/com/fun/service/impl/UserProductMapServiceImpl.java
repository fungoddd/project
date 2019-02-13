package com.fun.service.impl;

import com.fun.dao.UserProductMapMapper;
import com.fun.dao.UserShopMapMapper;
import com.fun.dto.userProductMap.UserProductMapExecution;
import com.fun.entity.UserProductMap;
import com.fun.entity.UserShopMap;
import com.fun.enums.UserProductMapStateEnum;
import com.fun.exceptions.UserProductMapOperationException;
import com.fun.service.UserProductMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-17 20:09:50
 * @Desc: 顾客消费商品的业务层实现
 */
@Service
public class UserProductMapServiceImpl implements UserProductMapService {

    @Autowired
    private UserProductMapMapper userProductMapMapper;
    @Autowired
    private UserShopMapMapper userShopMapMapper;

    /**
     * 根据查询条件(顾客id,商店id,顾客名,商品名,消费日期)查询用户消费商品映射的列表
     *
     * @param userProductCondition
     * @return
     */
    @Override
    public List<UserProductMap> getProductMap(UserProductMap userProductCondition) {

        if (userProductCondition != null) {
            return userProductMapMapper.selectUserProductMapList(userProductCondition);
        } else {
            return null;
        }
    }

    /**
     * 添加用户消费商品记录
     *
     * @param userProductMap
     * @return
     * @throws UserProductMapOperationException
     */
    @Override
    public UserProductMapExecution addUserProductMap(UserProductMap userProductMap) throws UserProductMapOperationException {
        if (userProductMap != null && userProductMap.getUser().getUserId() > 0 && userProductMap.getShop().getShopId() > 0
                && userProductMap.getProduct().getProductId() > 0) {
            userProductMap.setCreateTime(new Date());
            //添加商品记录
            try {
                int effectNum = userProductMapMapper.insertUserProductMap(userProductMap);
                if (effectNum <= 0) {
                    throw new UserProductMapOperationException("添加消费记录失败");
                }
                //如果此次消费会获得积分
                if (userProductMap.getPoint() != null && userProductMap.getPoint() > 0) {
                    //查询该顾客是否在商店消费国
                    UserShopMap userShopMap = userShopMapMapper.selectUserShopMapById(userProductMap.getUser().getUserId(),
                            userProductMap.getShop().getShopId());
                    if (userShopMap != null && userShopMap.getUserShopId() != null) {
                        //如果之前消费过,有在该商店积分记录,则进行总积分更新
                        userShopMap.setPoint(userShopMap.getPoint() + userProductMap.getPoint());
                        effectNum = userShopMapMapper.updateUserShopMapPoint(userShopMap);
                        if (effectNum <= 0) {
                            throw new UserProductMapOperationException("更新消费积分失败");
                        }
                    } else {
                        //没有消费记录新增商店积分信息
                        userShopMap.setUser(userProductMap.getUser());
                        userShopMap.setShop(userProductMap.getShop());
                        userShopMap.setPoint(userProductMap.getPoint());
                        effectNum = userShopMapMapper.insertUserShopMap(userShopMap);
                        if (effectNum <= 0) {
                            throw new UserProductMapOperationException("创建积分信息失败");
                        }
                    }
                }
                return new UserProductMapExecution(UserProductMapStateEnum.SUCCESS, userProductMap);
            } catch (Exception e) {
                throw new UserProductMapOperationException("addUserProductMap error:" + e.toString());
            }
        }
        return new UserProductMapExecution(UserProductMapStateEnum.NULL_USERPRODUCT_INFO);
    }
}
