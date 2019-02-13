package com.fun.entity;


import lombok.Data;

import java.util.Date;

//顾客消费的商品销量映射
@Data
public class ProductSellDaily {
    //主键
    private Integer productSellDailyId;
    //哪天的销量
    private Date createTime;
    //销量
    private Integer total;
    //商品信息
    private Product product;
    //商店信息
    private Shop shop;
}
