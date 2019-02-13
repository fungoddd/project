package com.fun.dao;

import com.fun.entity.ShopCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopCategoryMapper {
    /**
     * 根据查询条件(按层级)查询商品类别列表
     * @param shopCategoryCondition
     * @return List<ShopCategory>
     */
    List<ShopCategory>selectShopCategory(@Param("shopCategoryCondition") ShopCategory shopCategoryCondition);

}