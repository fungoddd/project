package com.fun.enums;

import lombok.Getter;

@Getter
public enum ShopAuthMapStateEnum {
    SUCCESS(1, "操作成功"), INNER_ERROR(-1001, "操作失败"), NULL_SHOPAUTH_ID(-1002,
            "ShopAuthId为空"), NULL_SHOPAUTH_INFO(-1003, "传入信息不能为空");

    private int state;

    private String stateInfo;

    private ShopAuthMapStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }


    public static ShopAuthMapStateEnum stateOf(int index) {
        for (ShopAuthMapStateEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}
