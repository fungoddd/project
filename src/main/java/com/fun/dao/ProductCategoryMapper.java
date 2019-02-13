package com.fun.dao;

import com.fun.entity.ProductCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductCategoryMapper {
    /**
     * 删除指定商店下的商品类别
     * @param productCategoryId
     * @param shopId
     * @return
     */
    int removeProductCategoryByPid(@Param("productCategoryId") Integer productCategoryId, @Param("shopId") Integer shopId);
    /**
     * 批量添加商品类别
     * @param productCategoryList
     * @return
     */
    int batchInsertProductCategory(List<ProductCategory> productCategoryList);
    /**
     * 通过shopId查询指定商店下的所有商品类别
     * @param shopId
     * @return List<ProductCategory>
     */
    List<ProductCategory> selectProductCategoryByShopId(Integer shopId);


}