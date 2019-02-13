package com.fun.dto.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 11:21:54
 * @Desc: 微信用户实体类
 */
@Data
public class WeChatUser implements Serializable {


    private static final long serialVersionUID = -2636259091433326119L;

    // openId，标识该公众号下面的该用户的唯一Id
    @JsonProperty("openid")
    private String openId;

    // 用户昵称
    @JsonProperty("nickname")
    private String nickName;

    // 性别
    @JsonProperty("sex")
    private int sex;

    // 省份
    @JsonProperty("province")
    private String province;

    //城市
    @JsonProperty("city")
    private String city;

    // 去
    @JsonProperty("country")
    private String country;

    // 头像图片地址
    @JsonProperty("headimgurl")
    private String headImgUrl;

    // 语言
    @JsonProperty("language")
    private String language;

    // 用户权限
    @JsonProperty("privilege")
    private String[] privilege;

}
