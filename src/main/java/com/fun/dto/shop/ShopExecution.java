package com.fun.dto.shop;

import com.fun.entity.Shop;
import com.fun.enums.ShopStateEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 12:02:24
 * @Desc: 商店对象的接收处理
 */
@Getter
@Setter
public class ShopExecution {
    //结果状态
    private int state;

    //标识状态
    private String stateInfo;

    //店铺数量
    private int count;

    //操作的shop(crud)
    private Shop shop;

    //shop列表(查询店铺列表)
    private List<Shop> shopList;

    public ShopExecution() {
    }

    //通过枚举构造(店铺操作失败的构造器)
    public ShopExecution(ShopStateEnum shopStateEnum) {
        this.state = shopStateEnum.getState();
        this.stateInfo = shopStateEnum.getStateInfo();
    }

    //店铺操作成功的构造器
    public ShopExecution(ShopStateEnum shopStateEnum, Shop shop) {
        this.state = shopStateEnum.getState();
        this.stateInfo = shopStateEnum.getStateInfo();
        this.shop = shop;
    }


    //店铺操作成功的构造器
    public ShopExecution(ShopStateEnum shopStateEnum, List<Shop> shopList) {
        this.state = shopStateEnum.getState();
        this.stateInfo = shopStateEnum.getStateInfo();
        this.shopList = shopList;
    }

   /* public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Shop getShop() {
        return shopAdmin;
    }

    public void setShop(Shop shopAdmin) {
        this.shopAdmin = shopAdmin;
    }

    public List<Shop> getShopList() {
        return shopList;
    }

    public void setShopList(List<Shop> shopList) {
        this.shopList = shopList;
    }*/
}
