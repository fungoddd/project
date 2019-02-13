package com.fun.enums;

import lombok.Getter;

@Getter
public enum LocalAuthStateEnum {
    LOGINFAIL(-1, "用户名或密码错误"), SUCCESS(0, "操作成功"), USER_NAME_ERROR(-1005, "用户名已被使用"), NULL_AUTH_INFO(-1006,
            "用户信息为空,请先登录在操作"), ONLY_ONE_ACCOUNT(-1007, "最多绑定一个帐号");

    private int state;

    private String stateInfo;

    private LocalAuthStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static LocalAuthStateEnum stateOf(int index) {
        for (LocalAuthStateEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }

}
