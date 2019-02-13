package com.fun.service;

import com.fun.dto.wechat.WechatAuthExecution;
import com.fun.entity.WechatAuth;
import com.fun.exceptions.WechatAuthOpeartionException;

/**
 * @Author: FunGod
 * @Date: 2018-12-08 01:07:09
 * @Desc: 微信用户操作接口
 */
public interface WechatAuthService {

    /**
     * 注册微信账号(本站服务号的微信号)
     * @param wechatAuth
     * @return
     */
    WechatAuthExecution register(WechatAuth wechatAuth) throws WechatAuthOpeartionException;
    /**
     * 通过openId查找平台的微信号用户
     *
     * @param openId
     * @return
     */
    WechatAuth getWechatUserByOpenId(String openId);

}
