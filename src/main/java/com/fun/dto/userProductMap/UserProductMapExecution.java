package com.fun.dto.userProductMap;

import com.fun.entity.UserProductMap;
import com.fun.enums.UserProductMapStateEnum;
import lombok.Data;

import java.util.List;
@Data
public class UserProductMapExecution {
	// 结果状态
	private int state;

	// 状态标识
	private String stateInfo;

	// 授权数
	private Integer count;

	// 操作的shopAuthMap
	private UserProductMap userProductMap;

	// 授权列表（查询专用）
	private List<UserProductMap> userProductMapList;

	public UserProductMapExecution() {
	}

	// 失败的构造器
	public UserProductMapExecution(UserProductMapStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	// 成功的构造器
	public UserProductMapExecution(UserProductMapStateEnum stateEnum,
			UserProductMap userProductMap) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userProductMap = userProductMap;
	}

	// 成功的构造器
	public UserProductMapExecution(UserProductMapStateEnum stateEnum,
			List<UserProductMap> userProductMapList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userProductMapList = userProductMapList;
	}




}
