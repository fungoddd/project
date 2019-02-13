package com.fun.dto.award;

import com.fun.entity.Award;
import com.fun.enums.AwardStateEnum;
import lombok.Data;
import java.util.List;
@Data
public class AwardExecution {
	// 结果状态
	private int state;

	// 状态标识
	private String stateInfo;

	// 店铺数量
	private int count;

	// 操作的award（增删改商品的时候用）
	private Award award;

	// 获取的award列表(查询商品列表的时候用)
	private List<Award> awardList;

	public AwardExecution() {
	}

	// 失败的构造器
	public AwardExecution(AwardStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	// 成功的构造器
	public AwardExecution(AwardStateEnum stateEnum, Award award) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.award = award;
	}

	// 成功的构造器
	public AwardExecution(AwardStateEnum stateEnum,
			List<Award> awardList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.awardList = awardList;
	}



}
