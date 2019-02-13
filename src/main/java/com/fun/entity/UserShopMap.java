package com.fun.entity;

import lombok.Data;

import java.util.Date;

//顾客和商店积分的映射
@Data
public class UserShopMap {
    //顾客信息
    private PersonInfo user;

    //商店信息
    private Shop shop;

    //主键
    private Integer userShopId;

    //顾客在该商店的积分
    private Integer point;


    private Integer userId;


    private Integer shopId;


    private String userName;


    private String shopName;


    private Date createTime;


}