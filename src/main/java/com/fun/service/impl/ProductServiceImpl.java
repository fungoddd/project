package com.fun.service.impl;

import com.fun.dao.ProductImgMapper;
import com.fun.dao.ProductMapper;
import com.fun.dto.product.ProductExecution;
import com.fun.entity.Product;
import com.fun.entity.ProductImg;
import com.fun.enums.ProductStateEnum;
import com.fun.exceptions.ProductOperationException;
import com.fun.service.ProductService;
import com.fun.util.Image.ImageHelper;
import com.fun.util.Image.ImageUtil;
import com.fun.util.Image.PathUtil;
import com.fun.util.page.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-02 01:10:48
 * @Desc: 商品操作的业务层实现
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImgMapper productImgMapper;

    /**
     * 删除指定商店下的商品,by ShopId And ProductId
     *
     * @param shopId
     * @param productId
     * @return ProductExecution
     */
    @Transactional
    @Override
    public ProductExecution deleteProductByShopIdAndPid(Integer shopId, Integer productId) throws ProductOperationException {
        if (shopId != null && productId != null) {
            try {
                //先获取数据库中该商品信息,获取缩略图地址进行删除
                Product tempProduct = productMapper.selectProductById(productId);

                if (tempProduct.getImgAddr() != null) {
                    //删除缩略图
                    ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
                    //删除详情图
                    removeProductImgList(productId);
                }

                int effectNum = productMapper.removeProductById(shopId, productId);
                if (effectNum <= 0) {
                    throw new ProductOperationException("删除商品类别失败");
                } else {
                    return new ProductExecution(ProductStateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new ProductOperationException("deleteProduct error" + e.toString());
            }
        }
        return new ProductExecution(ProductStateEnum.INNER_ERROR);
    }

    /**
     * 查询商品列表并分页:输入条件:模糊,商品状态,商品id和商品类别 PageHelper
     *
     * @param productCondition
     * @return ProductExecution
     */
    @Override
    public ProductExecution getProductListByPageHelper(Product productCondition) {

        List<Product> productList = productMapper.getProductList(productCondition);

        ProductExecution pe = new ProductExecution();

        if (productList != null && productList.size() > 0) {
            pe.setProductList(productList);

        } else {
            pe.setProductList(null);
            pe.setState(ProductStateEnum.EMPTY.getState());
        }

        return pe;
    }

    /**
     * 查询商品列表并分页:输入条件:模糊,商品状态,商品id和商品类别
     *
     * @param 暂时放弃使用//productCondition
     * @param pageIndex
     * @param pageSize
     * @return ProductExecution
     */
    @Override
    public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
        //页码转为数据库的行,进行查询
        int rowIndex = PageUtil.calculateRowIndex(pageIndex, pageSize);
        List<Product> productList = productMapper.selectProductList(productCondition, rowIndex, pageSize);
        //同样查询条件返回该条件下商品总数
        int count = productMapper.selectProductCount(productCondition);

        ProductExecution pe = new ProductExecution();

        if (productList != null && productList.size() > 0) {
            pe.setProductList(productList);
            pe.setCount(count);

        } else {
            pe.setState(ProductStateEnum.EMPTY.getState());
        }
        return pe;
    }

    /**
     * 查询商品信息通过商品id
     *
     * @param productId
     * @return Product
     */
    @Override
    public Product getProductById(Integer productId) {
        Product product = productMapper.selectProductById(productId);
        String name = product.getProductDesc();

        return product;
    }

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
      /*
    1.如果缩略图不为空,处理缩略图,获取缩略图相对路径赋给product商品
    如果之前存在缩略图则先删除在更新新的缩略图,商品详情图同理
    2.向tb_product商品数据库表中插入商品信息,获取productId商品id
    3.通过productId批量处理商品详情图
    4.将商品详情图插入到tb_product_img数据库表中
     */
    @Override
    @Transactional
    public ProductExecution updateProduct(Product product, ImageHelper thumbnail, List<ImageHelper> productImgList) throws ProductOperationException {

        if (product != null && product.getShop() != null && product.getShop().getShopId() > 0) {

            //给商品添加默认属性
            product.setLastEditTime(new Date());

            //如果上传商品缩略图不为空并且原有缩略图不为空则删除原有缩略图添加
            if (thumbnail != null) {
                //先获取数据库中该商品信息,获取缩略图地址进行删除
                Product tempProduct = productMapper.selectProductById(product.getProductId());

                if (tempProduct.getImgAddr() != null) {
                    ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
                }
                //添加商品缩略图
                addThumbnail(product, thumbnail);
            }
            //如果上传详情图不为空,将原有的详情图删除,添加新的图
            if (productImgList != null && productImgList.size() > 0) {

                removeProductImgList(product.getProductId());
                addProductImgList(product, productImgList);

            }
            try {
                //修改商品信息操作
                int effectNum = productMapper.updateProduct(product);

                if (effectNum <= 0) {
                    throw new ProductOperationException("修改商品信息失败");
                }
                return new ProductExecution(ProductStateEnum.SUCCESS, product);

            } catch (Exception e) {
                throw new ProductOperationException("修改商品失败:" + e.toString());
            }
        } else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }


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
    /*
    1.处理缩略图,获取缩略图相对路径赋给product商品
    2.向tb_product商品数据库表中插入商品信息,获取productId商品id
    3.通过productId批量处理商品详情图
    4.将商品详情图插入到tb_product_img数据库表中
     */
    @Override
    @Transactional
    public ProductExecution addProduct(Product product, ImageHelper thumbnail, List<ImageHelper> productImgList)
            throws ProductOperationException {

        if (product != null && product.getShop() != null && product.getShop().getShopId() > 0) {

            //给商品添加默认信息,创建时间,最后更改时间,状态默认为上架1
            product.setCreateTime(new Date());
            product.setLastEditTime(new Date());
            product.setEnableStatus(1);

            try {
                //商品缩略图不为空就添加
                if (thumbnail != null) {
                    addThumbnail(product, thumbnail);
                }

                //添加商品信息
                int effectNum = productMapper.insertProduct(product);

                if (effectNum <= 0) {
                    throw new ProductOperationException("创建商品失败");
                }

            } catch (Exception e) {

                throw new ProductOperationException("insertProduct error:" + e.toString());
            }

            //如果商品详情图不为空添加
            if (productImgList != null && productImgList.size() > 0) {
                addProductImgList(product, productImgList);
            }

            return new ProductExecution(ProductStateEnum.SUCCESS, product);

        } else {
            //否则添加商品为空返回信息
            return new ProductExecution(ProductStateEnum.EMPTY);
        }

    }


/////////////////////////辅助方法

    /**
     * 删除指定商品下的所有详情图
     *
     * @param productId
     */
    private void removeProductImgList(Integer productId) {
        try {
            // 通过productId获取原来的图片
            List<ProductImg> productImgList = productImgMapper.selectProductImg(productId);
            // 物理删除原来的图片
            for (ProductImg productImg : productImgList) {
                ImageUtil.deleteFileOrPath(productImg.getImgAddr());
            }
            // 删除数据库里原有图片的信息
            productImgMapper.removeProductImgByProductId(productId);

        } catch (Exception e) {
            throw new ProductOperationException("删除详情图失败!" + e.toString());
        }
    }

    /**
     * 批量添加商品详情图
     *
     * @param product
     * @param productImgHelperList
     */

    private void addProductImgList(Product product, List<ImageHelper> productImgHelperList) throws ProductOperationException {

        //获取图片存储路径,直接存放到相应的商店文件夹下
        String relativeImgPath = PathUtil.getShopImgPath(product.getShop().getShopId());

        //创建一个详情图的list接收处理过的详情图
        List<ProductImg> productImgList = new ArrayList<>();

        //遍历所有合并处理过的商品详情图(imgHelper类型的),添加到productImg商品详情图实体类中
        for (ImageHelper imageHelper : productImgHelperList) {

            //处理图片上传到对应创建生成目录下
            String imgAddr = ImageUtil.generateNormalImg(imageHelper, relativeImgPath);

            //给商品详情图设置图片地址和商品id等信息
            ProductImg productImg = new ProductImg();
            productImg.setImgAddr(imgAddr);
            productImg.setProductId(product.getProductId());
            productImg.setCreateTime(new Date());

            product.setCreateTime(new Date());
            product.setLastEditTime(new Date());

            //添加到详情图的list集合中
            productImgList.add(productImg);

        }

        //如果添加的图片不为空进行批量添加
        if (productImgList.size() > 0) {
            try {
                int effectNum = productImgMapper.batchAddProductImg(productImgList);

                if (effectNum <= 0) {
                    throw new ProductOperationException("添加商品详情图失败");
                }
            } catch (Exception e) {
                throw new ProductOperationException("batchInsertProductImg error:" + e.toString());
            }
        }
    }

    /**
     * 添加商品缩略图
     *
     * @param product
     * @param thumbnail
     */
    private void addThumbnail(Product product, ImageHelper thumbnail) {

        try {
            //获得商品下的缩略图子路径
            String relativeImgPath = PathUtil.getShopImgPath(product.getShop().getShopId());

            //图片真实路径,传入图片文件流和图片子路径
            String thumbnailPath = ImageUtil.generateThumbnail(thumbnail, relativeImgPath);

            //商品设置图片
            product.setImgAddr(thumbnailPath);

        } catch (Exception e) {

            throw new ProductOperationException("添加缩略图失败!" + e.toString());
        }
    }


}
