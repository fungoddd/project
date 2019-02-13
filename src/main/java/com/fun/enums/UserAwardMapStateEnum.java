package com.fun.enums;

import lombok.Getter;

@Getter
public enum UserAwardMapStateEnum {
	SUCCESS(1, "操作成功"), INNER_ERROR(-1001, "操作失败"), NULL_USERAWARD_ID(-1002,
			"UserAwardId为空"), NULL_USERAWARD_INFO(-1003, "传入信息不能为空");

	private int state;

	private String stateInfo;

	private UserAwardMapStateEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}



	public static UserAwardMapStateEnum stateOf(int index) {
		for (UserAwardMapStateEnum state : values()) {
			if (state.getState() == index) {
				return state;
			}
		}
		return null;
	}
}
