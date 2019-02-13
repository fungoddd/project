package com.fun.service;

import com.fun.dto.product.ProductExecution;
import com.fun.entity.Product;
import com.fun.exceptions.ProductOperationException;
import com.fun.util.Image.ImageHelper;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-05 15:27:39
 * @Desc: 商品的操作接口
 */
public interface ProductService {
    /**
     * 删除指定商店下的商品,by ShopId And ProductId
     *
     * @param shopId
     * @param productId
     * @return ProductExecution
     * @throws ProductOperationException
     */
    ProductExecution deleteProductByShopIdAndPid(Integer shopId, Integer productId) throws ProductOperationException;

    /**
     * PageHelper:查询商品列表并分页:输入条件:模糊,商品状态,商品id和商品类别
     *
     * @param productCondition
     * @return ProductExecution
     */
    ProductExecution getProductListByPageHelper(Product productCondition);

    /**
     * 查询商品列表并分页:输入条件:模糊,商品状态,商品id和商品类别
     *
     * @param 暂时放弃使用//productCondition
     * @param pageIndex
     * @param pageSize
     * @return ProductExecution
     */
    ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize);

    /**
     * 查询商品信息通过商品id
     *
     * @param productId
     * @return Product
     */
    Product getProductById(Integer productId);


    /**
     * 修改商品信息包括图片处理
     *
     * @param product
     * @param thumbnail      缩略图
     * @param productImgList 详情图
     * @return ProductExecution
     * @throws ProductOperationException
     * @ImageHelper List<InputStream> productImgList,List<String> productImgNameList
     * InputStream thumbnail, String thumbnailName(ImageHelper把图片的InputStream和fileName封装起来)
     */

    ProductExecution updateProduct(Product product, ImageHelper thumbnail, List<ImageHelper> productImgList)
            throws ProductOperationException;

    /**
     * 添加商品信息包括图片处理
     *
     * @param product
     * @param thumbnail      缩略图
     * @param productImgList 详情图
     * @return ProductExecution
     * @throws ProductOperationException
     * @ImageHelper List<InputStream> productImgList,List<String> productImgNameList
     * InputStream thumbnail, String thumbnailName(ImageHelper把图片的InputStream和fileName封装起来)
     */

    ProductExecution addProduct(Product product, ImageHelper thumbnail, List<ImageHelper> productImgList)
            throws ProductOperationException;


}
