package com.fun.service;

import com.fun.entity.ProductSellDaily;

import java.util.Date;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-17 19:00:40
 * @Desc: 统计顾客消费商品销量接口
 */
public interface ProductSellDailyService {
    /**
     * 每日定时对所有商店的销量进行统计
     */
    void dailyCalculate();

    /**
     * 通过查询条件(商店id,商品名,时间段)获取商品日销售的统计列表
     * @param productSellDaily
     * @param beginTime
     * @param endTime
     * @return List<ProductSellDaily>
     */
    List<ProductSellDaily>getProductSellDailyList(ProductSellDaily productSellDaily, Date beginTime,Date endTime);
}
