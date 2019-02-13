package com.fun.service;


import com.fun.dto.userProductMap.UserProductMapExecution;
import com.fun.entity.UserProductMap;
import com.fun.exceptions.UserProductMapOperationException;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-17 20:07:47
 * @Desc: 用户消费商品接口
 */
public interface UserProductMapService {

    /**
     * 根据查询条件(顾客id,商店id,顾客名,商品名,消费日期)查询用户消费商品映射的列表
     * @param userProductCondition
     * @return
     */
    List<UserProductMap> getProductMap(UserProductMap userProductCondition);

    /**
     * 添加用户消费商品记录
     * @param userProductMap
     * @return
     * @throws UserProductMapOperationException
     */
    UserProductMapExecution addUserProductMap(UserProductMap userProductMap)throws UserProductMapOperationException;
}
