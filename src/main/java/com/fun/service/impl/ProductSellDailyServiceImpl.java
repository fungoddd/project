package com.fun.service.impl;

import com.fun.dao.ProductSellDailyMapper;
import com.fun.entity.ProductSellDaily;
import com.fun.service.ProductSellDailyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-17 19:03:32
 * @Desc: 统计所有商店商品销量业务层实现
 */
@Service
public class ProductSellDailyServiceImpl implements ProductSellDailyService {
    private static final Logger log = LoggerFactory.getLogger(ProductSellDailyService.class);
    @Autowired
    private ProductSellDailyMapper productSellDailyMapper;

    /**
     * 每天统计一下商店的商品销量
     */
    @Override
    @Transactional
    public void dailyCalculate() {
        log.info("Quartz Running...");
        try {
            //统计每个产生销量的商店下的商品日销量
            productSellDailyMapper.insertProductSellDaily();
            //统计剩余的商品日效率,全部为0
            productSellDailyMapper.insertDefaultProductSellDaily();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过查询条件(商店id,商品名,时间段)获取商品日销售的统计列表
     *
     * @param productSellDaily
     * @param beginTime
     * @param endTime
     * @return List<ProductSellDaily>
     */
    @Override
    public List<ProductSellDaily> getProductSellDailyList(ProductSellDaily productSellDaily, Date beginTime, Date endTime) {

        return productSellDailyMapper.selectProductSellDaily(productSellDaily, beginTime, endTime);
    }
}
