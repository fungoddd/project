package com.fun.entity;

import lombok.Data;

import java.util.Date;

//商店授权
@Data
public class ShopAuthMap {
    //商店实体信息
    private Shop shop;
    //员工实体信息
    private PersonInfo employee;
    //主键
    private Integer shopAuthId;
    //员工实体id
    private Integer employeeId;
    //商店id
    private Integer shopId;
    //用户名
    private String name;
    //职称名
    private String title;
    //职称flag(用于权限控制)
    private Integer titleFlag;
    //创建时间
    private Date createTime;
    //更新时间
    private Date lastEditTime;
    //授权有效状态:O无效1有效
    private Integer enableStatus;


}