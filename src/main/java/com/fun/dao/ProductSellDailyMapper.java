package com.fun.dao;


import com.fun.entity.ProductSellDaily;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-13 12:15:37
 * @Desc: 顾客消费的商品销售量映射dao
 */
public interface ProductSellDailyMapper {

    /**
     * 通过查询条件(商店id,商品名,时间段)获取商品日销售的统计列表
     *
     * @param productSellDailyCondition
     * @param beginTime
     * @param endTime
     * @return List<ProductSellDaily>
     */
    List<ProductSellDaily> selectProductSellDaily(@Param("productSellDailyCondition") ProductSellDaily productSellDailyCondition,
                                                  @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    /**
     * 统计平台所有商品的日销量(第二天统计前一天的,精确到年月日,按照商品分组)
     * 添加的数据来自tb_user_product_map顾客消费商品映射
     *
     * @return
     */
    int insertProductSellDaily();

    /**
     * 销量为0插入默认数据
     *
     * @return
     */
    int insertDefaultProductSellDaily();

}
