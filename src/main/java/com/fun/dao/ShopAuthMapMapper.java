package com.fun.dao;

import com.fun.entity.ShopAuthMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-13 15:34:27
 * @Desc: 商店授权信息dao
 */
public interface ShopAuthMapMapper {

    /**
     * 通过商店id查询指定商店下的授权信息
     *
     * @param shopId
     * @return
     */
    List<ShopAuthMap> selectShopAuthMapListByShopId(Integer shopId);

    /**
     * 新增商店与员工授权关系
     *
     * @param shopAuthMap
     * @return
     */
    int insertShopAuthMap(ShopAuthMap shopAuthMap);

    /**
     * 更新商店授权信息(职称,职称flag,可用状态)
     *
     * @param shopAuthMap
     * @return
     */
    int updateShopAuthMap(ShopAuthMap shopAuthMap);

    /**
     * 删除对某员工的授权信息
     *
     * @param shopAuthId
     * @return
     */
    int deleteShopAuthMap(@Param("shopAuthId") Integer shopAuthId, @Param("shopId") Integer shopId);

    /**
     * 通过主键查询商店对某员工的授权信息
     *
     * @param shopAuthId
     * @return
     */
    ShopAuthMap selectShopAuthMapById(Integer shopAuthId);
}