package com.fun.dto.userAwardMap;

import com.fun.entity.UserAwardMap;
import com.fun.enums.UserAwardMapStateEnum;
import lombok.Data;

import java.util.List;
@Data
public class UserAwardMapExecution {
	// 结果状态
	private int state;

	// 状态标识
	private String stateInfo;

	// 授权数
	private Integer count;

	// 操作的UserAwardMap
	private UserAwardMap userAwardMap;

	// 授权列表（查询专用）
	private List<UserAwardMap> userAwardMapList;

	public UserAwardMapExecution() {
	}

	// 失败的构造器
	public UserAwardMapExecution(UserAwardMapStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	// 成功的构造器
	public UserAwardMapExecution(UserAwardMapStateEnum stateEnum,
			UserAwardMap userAwardMap) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userAwardMap = userAwardMap;
	}

	// 成功的构造器
	public UserAwardMapExecution(UserAwardMapStateEnum stateEnum,
			List<UserAwardMap> userAwardMapList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userAwardMapList = userAwardMapList;
	}



}
