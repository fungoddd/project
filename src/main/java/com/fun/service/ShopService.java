package com.fun.service;

import com.fun.dto.shop.ShopExecution;
import com.fun.entity.Shop;
import com.fun.exceptions.ShopOperationException;
import com.fun.util.Image.ImageHelper;

public interface ShopService {
    /**
     * 分页查询店铺PageHelper
     *
     * @return
     */
    ShopExecution selectShopListByPageHelper(Shop shopCondition);

    /**
     * 分页查询店铺:(条件查询)店铺名(模糊),店铺状态,店铺类别,区域id,owner
     *
     * @param 暂时放弃使用//shopCondition
     * @param pageIndex     从第几页获取数据
     * @param pageSize      每页返回条数
     * @return ShopExecution
     */
    //ShopExecution selectShopList(Shop shopCondition, int pageIndex, int pageSize);

    /**
     * selectShopList(该用户的店铺总数)查到的总数
     *
     * @param shopCondition
     * @return int shopCount
     */



    /**
     * 通过商店id获取商店信息,多表关联查询
     *
     * @param id
     * @return Shop
     */
    Shop getShopById(Integer id);


    /**
     * 修改商店信息包括图片处理
     *
     * @param shop
     * @param thumbnail 商品缩略图 ImageHelper把shopImgInputStream fileName封装起来
     * @return ShopExecution
     * @throws ShopOperationException
     */
    ShopExecution updateShop(Shop shop, ImageHelper thumbnail) throws ShopOperationException;


    /**
     * 注册商店信息包括图片处理
     *
     * @param shop
     * @param thumbnail 商品缩略图 ImageHelper把shopImgInputStream fileName封装起来
     * @return ShopExecution
     * @throws ShopOperationException
     */
    ShopExecution addShop(Shop shop, ImageHelper thumbnail) throws ShopOperationException;
}
