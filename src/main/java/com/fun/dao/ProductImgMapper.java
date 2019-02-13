package com.fun.dao;

import com.fun.entity.ProductImg;

import java.util.List;

public interface ProductImgMapper {
    /**
     * 查询指定商品图片列表
     *
     * @param productId
     * @return List<ProductImg>
     */
    List<ProductImg> selectProductImg(Integer productId);

    /**
     * 删除指定商品下的商品图片
     *
     * @param productId
     * @return
     */
    int removeProductImgByProductId(Integer productId);

    /**
     * 批量商品添加商品图片
     *
     * @param productImgList
     * @return
     */
    int batchAddProductImg(List<ProductImg> productImgList);



}