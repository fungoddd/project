package com.fun.enums;

import lombok.Getter;

@Getter
public enum UserProductMapStateEnum {
    SUCCESS(1, "操作成功"), INNER_ERROR(-1001, "操作失败"), NULL_USERPRODUCT_ID(-1002,
            "UserProductId为空"), NULL_USERPRODUCT_INFO(-1003, "传入信息不能为空");

    private int state;

    private String stateInfo;

    private UserProductMapStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }


    public static UserProductMapStateEnum stateOf(int index) {
        for (UserProductMapStateEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}
