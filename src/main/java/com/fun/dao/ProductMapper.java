package com.fun.dao;

import com.fun.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {

    /**
     * 获取商店列表分页,通过PageHelper
     *
     * @param product
     * @return List<Product>
     */
    List<Product> getProductList(@Param("productCondition") Product product);

    /**
     * 查询商品列表并分页，可输入的条件有：商品名（模糊），商品状态，店铺id，商品类别
     *
     * @param 暂时放弃使用//productCondition
     * @param rowIndex
     * @param pageSize
     * @return List<Product>
     */
    List<Product> selectProductList(@Param("productCondition") Product productCondition,
                                    @Param("rowIndex") int rowIndex,
                                    @Param("pageSize") int pageSize);

    /**
     * 查询对应的商品总数
     *
     * @param 暂时放弃使用//productCondition
     * @return
     */
    int selectProductCount(@Param("productCondition") Product productCondition);

    /**
     * 删除指定商店下的指定商品
     *
     * @param productId
     * @return
     */
    int removeProductById(@Param("shopId") Integer shopId, @Param("productId") Integer productId);

    /**
     * 删除商品类别之前，将商品信息中的商品类别id置空
     *
     * @param productCategoryId
     * @return
     */
    int updateProductCategoryToNull(Integer productCategoryId);
    /**
     * 通过商品id删除商品详情图
     * @param productId
     * @return
     */

    /**
     * 通过商品id查询指定商品信息(包括包括所属商店,商品类别和图片)
     *
     * @param productId
     * @return Product
     */
    Product selectProductById(Integer productId);

    /**
     * 更改商品信息
     *
     * @param product
     * @return
     */
    int updateProduct(Product product);

    /**
     * 添加商品
     *
     * @param product
     * @return
     */
    int insertProduct(Product product);




}