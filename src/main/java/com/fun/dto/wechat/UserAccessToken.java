package com.fun.dto.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author: FunGod
 * @Date: 2018-12-7 11:43:52
 * @Desc: 用户授权token,用来接收accesstoken以及openid等信息
 * 一个访问令牌包含了此登陆会话的安全信息。当用户登陆时，系统创建一个访问令牌，
 * 然后以该用户身份运行的的所有进程都拥有该令牌的一个拷贝。该令牌唯一表示该用户、用户的组和用户的特权。
 */
@Data
public class UserAccessToken {// 获取到的凭证
    @JsonProperty("access_token")
    private String accessToken;
    // 凭证有效时间，单位：秒
    @JsonProperty("expires_in")
    private String expiresIn;
    // 表示更新令牌，用来获取下一次的访问令牌
    @JsonProperty("refresh_token")
    private String refreshToken;
    // 该用户在此公众号下的身份标识，对于此微信号具有唯一性
    @JsonProperty("openid")
    private String openId;
    // 表示权限范围，这里可省略
    @JsonProperty("scope")
    private String scope;


}
