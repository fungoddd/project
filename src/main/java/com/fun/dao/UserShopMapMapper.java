package com.fun.dao;

import com.fun.entity.UserShopMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-13 13:28:55
 * @Desc: 顾客和商店积分的映射dao
 */
public interface UserShopMapMapper {

    /**
     * 根据查询条件(商店id,顾客id,商店名,顾客名,创建时间范围)查询顾客和商店积分关系的列表
     *
     * @param userShopMap
     * @return
     */
    List<UserShopMap> selectUserShopMapList(UserShopMap userShopMap);

    /**
     * 通过顾客id和商店id查询该用户在指定商店的积分信息
     *
     * @param userId
     * @param shopId
     * @return
     */
    UserShopMap selectUserShopMapById(@Param("userId") Integer userId, @Param("shopId") Integer shopId);

    /**
     * 新增一条顾客在商店的积分记录
     * @param userShopMap
     * @return
     */
    int insertUserShopMap(UserShopMap userShopMap);
    /**
     * 更新用户在某商店的积分(通过用户和商店id)
     *
     * @param userShopMap
     * @return
     */
    int updateUserShopMapPoint(UserShopMap userShopMap);
}