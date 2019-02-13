package com.fun.service;

import com.fun.dto.localAuth.LocalAuthExecution;
import com.fun.entity.LocalAuth;
import com.fun.exceptions.LocalAuthOperationException;

/**
 * @Author: FunGod
 * @Date: 2018-12-09 14:56:28
 * @Desc: 平台账号操作接口(登录注册修改密码等)
 */
public interface LocalAuthService {

    /**
     * 平台用户登录
     *
     * @param username
     * @param password
     * @return LocalAuth
     */
    LocalAuth localAuthLogin(String username, String password);

    /**
     * 通过平台号里的外键userId获取用户信息
     *
     * @param userId
     * @return
     */
    LocalAuth getLocalAuthByUserId(Integer userId);

    /**
     * 绑定微信,注册平台账号
     *
     * @param localAuth
     * @return LocalAuthExecution
     * @throws LocalAuthOperationException
     */
    LocalAuthExecution bindLocalAuthWithWechat(LocalAuth localAuth) throws LocalAuthOperationException;


    /**
     *  修改平台账号的密码
     * @param userId
     * @param username
     * @param password
     * @param newPassword
     * @return LocalAuthExecution
     * @throws LocalAuthOperationException
     */
    LocalAuthExecution updateLocalAuthPwd(Integer userId, String username, String password, String newPassword) throws LocalAuthOperationException;

}