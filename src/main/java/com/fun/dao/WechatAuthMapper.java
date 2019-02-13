package com.fun.dao;

import com.fun.entity.WechatAuth;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 23:05:30
 * @Desc: 微信用户
 */
public interface WechatAuthMapper {

    /**
     * 添加微信用户
     *
     * @param wechatAuth
     * @return
     */
    int insertWechatUser(WechatAuth wechatAuth);

    /**
     * 通过openId查询微信用户
     *
     * @param openId
     * @return
     */
    WechatAuth selectWechatUserInfoByOpenId(String openId);

}