package com.fun.dto.wechat;

import com.fun.entity.WechatAuth;
import com.fun.enums.WechatAuthStateEnum;
import lombok.Data;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-08 01:12:581
 * @Desc: 微信用户对象接收处理
 */
@Data
public class WechatAuthExecution {
    // 结果状态
    private int state;

    // 状态标识
    private String stateInfo;

    private int count;

    private WechatAuth wechatAuth;

    private List<WechatAuth> wechatAuthList;

    public WechatAuthExecution() {
    }

    // 失败的构造器
    public WechatAuthExecution(WechatAuthStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    // 成功的构造器
    public WechatAuthExecution(WechatAuthStateEnum stateEnum, WechatAuth wechatAuth) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.wechatAuth = wechatAuth;
    }

    // 成功的构造器
    public WechatAuthExecution(WechatAuthStateEnum stateEnum,
                               List<WechatAuth> wechatAuthList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.wechatAuthList = wechatAuthList;
    }
}
