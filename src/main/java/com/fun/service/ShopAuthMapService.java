package com.fun.service;

import com.fun.dto.shopAuthMap.ShopAuthMapExecution;
import com.fun.entity.ShopAuthMap;
import com.fun.exceptions.ShopAuthMapOperationException;

/**
 * @Author: FunGod
 * @Date: 2018-12-13 18:27:07
 * @Desc: 商店员工授权接口
 */
public interface ShopAuthMapService {
    /**
     * 通过商店id获取该商店对员工的授权信息列表
     *
     * @param shopId
     * @return
     */
    ShopAuthMapExecution getShopAuthMapListByShopId(Integer shopId);

    /**
     * 通过主键获取指定的商店授权信息
     *
     * @param shopAuthId
     * @return
     */
    ShopAuthMap getShopAuthMapById(Integer shopAuthId);

    /**
     * 添加商店授权信息
     *
     * @param shopAuthMap
     * @return
     * @throws ShopAuthMapOperationException
     */
    ShopAuthMapExecution addShopAuthMap(ShopAuthMap shopAuthMap) throws ShopAuthMapOperationException;

    /**
     * 更新商店授权信息(职称,职称flag,可用状态)
     *
     * @param shopAuthMap
     * @return
     * @throws ShopAuthMapOperationException
     */
    ShopAuthMapExecution updateShopAuthMap(ShopAuthMap shopAuthMap) throws ShopAuthMapOperationException;

    /**
     * 删除指定商店授权信息
     *
     * @param shopAuthId
     * @return
     * @throws ShopAuthMapOperationException
     */
    ShopAuthMapExecution removeShopAuthMap(Integer shopAuthId,Integer shopId) throws ShopAuthMapOperationException;

}
