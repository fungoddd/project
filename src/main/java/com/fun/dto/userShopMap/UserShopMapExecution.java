package com.fun.dto.userShopMap;

import com.fun.entity.UserShopMap;
import com.fun.enums.UserShopMapStateEnum;

import lombok.Data;

import java.util.List;

@Data
public class UserShopMapExecution {
    // 结果状态
    private int state;

    // 状态标识
    private String stateInfo;

    // 授权数
    private Integer count;

    // 操作的UserShopMap
    private UserShopMap userShopMap;

    // 授权列表（查询专用）
    private List<UserShopMap> userShopMapList;

    public UserShopMapExecution() {
    }

    // 失败的构造器
    public UserShopMapExecution(UserShopMapStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    // 成功的构造器
    public UserShopMapExecution(UserShopMapStateEnum stateEnum,
                                UserShopMap userShopMap) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.userShopMap = userShopMap;
    }

    // 成功的构造器
    public UserShopMapExecution(UserShopMapStateEnum stateEnum,
                                List<UserShopMap> userShopMapList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.userShopMapList = userShopMapList;
    }


}
