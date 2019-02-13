package com.fun.service.impl;

import com.fun.dao.ProductCategoryMapper;
import com.fun.dao.ProductMapper;
import com.fun.dto.productCategory.ProductCategoryExecution;
import com.fun.entity.ProductCategory;
import com.fun.enums.ProductCategoryStateEnum;
import com.fun.exceptions.ProductCategoryOperationException;
import com.fun.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-02 01:26:44
 * @Desc: 商品类别的业务层实现
 */
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    /**
     * 删除商品类别(先将该类别下商品里的商品类别id设为null)
     *
     * @param productCategoryId
     * @param shopId
     * @return ProductCategoryExecution
     * @throws ProductCategoryOperationException
     */
    @Transactional
    @Override
    public ProductCategoryExecution removeProductCategory(Integer productCategoryId, Integer shopId) throws ProductCategoryOperationException {

        //TODO 将该类别下商品里的商品类别id设为null
        if (productCategoryId != null && shopId != null) {

            try {
                //将该类别下商品里的商品类别id设为null,解除商品类别和商品的关系
                int num = productMapper.updateProductCategoryToNull(productCategoryId);
                //刪除商品类别
                int effectNum = productCategoryMapper.removeProductCategoryByPid(productCategoryId, shopId);

                if (effectNum <= 0 || num < 0) {

                    throw new ProductCategoryOperationException("删除商品类别失败");

                } else {

                    return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
                }

            } catch (Exception e) {

                throw new ProductCategoryOperationException("deleteProductCategory error:" + e.getMessage());
            }

        } else {

            return new ProductCategoryExecution(ProductCategoryStateEnum.INNER_ERROR);
        }
    }

    /**
     * 批量添加商品类别
     *
     * @param productCategoryList
     * @return ProductCategoryExecution
     * @throws ProductCategoryOperationException
     */
    @Transactional
    @Override
    public ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList) throws ProductCategoryOperationException {

        if (productCategoryList != null && productCategoryList.size() > 0) {

            try {
                int effectNum = productCategoryMapper.batchInsertProductCategory(productCategoryList);

                if (effectNum <= 0) {

                    throw new ProductCategoryOperationException("商店类别添加失败!");

                } else {

                    return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
                }

            } catch (Exception e) {

                throw new ProductCategoryOperationException("batchInsertProductCategory error:" + e.getMessage());
            }
        } else {

            return new ProductCategoryExecution(ProductCategoryStateEnum.EMPTY_LIST);
        }
    }

    /**
     * 通过shopId查询指定商店下的所有商品类别
     *
     * @return List<ProductCategory>
     */
    @Override
    public List<ProductCategory> getProductCategoryList(Integer shopId) {

        return productCategoryMapper.selectProductCategoryByShopId(shopId);
    }
}
