package com.fun.enums;

import lombok.Getter;


/**
 * @Author: FunGod
 * @Date: 2018-12-07 12:05:11
 * @Desc: 商店枚举信息,返回结果状态的封装处理
 */
@Getter
public enum ShopStateEnum {

    CHECK(0, "审核中"), OFFLINE(-1, "非法店铺"), SUCCESS(1, "操作成功"), PASS(2, "通过认证"),
    INNER_ERROR(-1001, "系统内部错误"), NULL_SHOPID(-1002, "商铺ID为空"), NULL_SHOP(-1003, "商铺信息为空");
    //结果状态
    private int state;
    //标识状态
    private String stateInfo;

    private ShopStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    /**
     * 根据传入的state返回相应的enum值
     */
    public static ShopStateEnum stateOf(int state) {
        for (ShopStateEnum shopStateEnum : values()) {
            if (shopStateEnum.getStateInfo() == null) {
                return shopStateEnum;
            }
        }
        return null;
    }
}
