package com.fun.dao;

import com.fun.entity.Shop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopMapper {
    /**
     * 获取商店列表分页,通过PageHelper,模糊查询
     *
     * @param shop
     * @return
     */
    List<Shop> getShopList(@Param("shopCondition") Shop shop);

    /**
     * 分页查询店铺:(条件查询)店铺名(模糊),店铺状态,店铺类别,区域id,owner
     *
     * @param 暂时放弃使用//shopCondition
     * @param rowIndex              从第几行获取数据
     * @param pageSize              返回条数
     * @return
     */
    List<Shop> selectShopList(@Param("shopCondition") Shop shopCondition,
                              @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

    /**
     * selectShopList(该用户的店铺总数)查到的总数
     *
     * @param 暂时放弃使用//shopCondition
     * @return
     */

    //int selectShopCount(@Param("shopCondition") Shop shopCondition);

    /**
     * 根据id查询店铺
     *
     * @param shopId
     * @return
     */
    Shop selectShopById(Integer shopId);

    /**
     * 新增店铺
     *
     * @param shop
     * @return
     */
    int insertShop(Shop shop);

    /**
     * 更新店铺
     *
     * @param shop
     * @return
     */
    int updateShop(Shop shop);


}