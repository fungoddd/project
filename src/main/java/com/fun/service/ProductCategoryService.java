package com.fun.service;

import com.fun.dto.productCategory.ProductCategoryExecution;
import com.fun.entity.ProductCategory;
import com.fun.exceptions.ProductCategoryOperationException;

import java.util.List;

public interface ProductCategoryService {
    /**
     * 删除商品类别(先将该类别下商品里的商品类别id设为null)
     *
     * @param productCategoryId
     * @param shopId
     * @return ProductCategoryExecution
     * @throws ProductCategoryOperationException
     */
    ProductCategoryExecution removeProductCategory(Integer productCategoryId, Integer shopId) throws ProductCategoryOperationException;

    /**
     * 批量添加商品类别
     *
     * @param productCategoryList
     * @return ProductCategoryExecution
     * @throws ProductCategoryOperationException
     */
    ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList) throws ProductCategoryOperationException;

    /**
     * 通过shopId查询指定商店下的所有商品类别
     *
     * @param shopId
     * @return List<ProductCategory>
     */

    List<ProductCategory> getProductCategoryList(Integer shopId);

}
