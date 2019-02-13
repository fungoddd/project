package com.fun.service;

import com.fun.entity.ShopCategory;

import java.util.List;

public interface ShopCategoryService {
    //redis的前缀key
    public static final String SHOP_CATEGORY_LIST_KEY = "shopcategorylist";

    /**
     * 获取商品类别列表,先从redis中缓存中取
     * @param shopCategoryCondition
     * @return
     */
    List<ShopCategory>getShopCategoryList(ShopCategory shopCategoryCondition);
}
