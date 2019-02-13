package com.fun.service.impl;

import com.fun.dao.LocalAuthMapper;
import com.fun.dto.localAuth.LocalAuthExecution;
import com.fun.entity.LocalAuth;
import com.fun.enums.LocalAuthStateEnum;
import com.fun.exceptions.LocalAuthOperationException;
import com.fun.service.LocalAuthService;
import com.fun.util.LocalAuth.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * @Author: FunGod
 * @Date: 2018-12-09 15:46:18
 * @Desc: 平台账号操作业务层实现(登录注册修改密码等)
 */
@Service
public class LocalAuthServiceImpl implements LocalAuthService {

    @Autowired
    private LocalAuthMapper localAuthMapper;

    /**
     * 平台用户登录
     *
     * @param username
     * @param password
     * @return LocalAuth
     */
    @Override
    public LocalAuth localAuthLogin(String username, String password) {
        //用户登录,因为数据库中的密码注册时进行过加密,所以用户登录要传入加密过的密码保证一致
        return localAuthMapper.localAuthLogin(username, MD5.getMd5(password));
    }

    /**
     * 通过平台号里的外键userId获取用户信息
     *
     * @param userId
     * @return
     */

    @Override
    public LocalAuth getLocalAuthByUserId(Integer userId) {
        return localAuthMapper.selectLocalAuthByUserId(userId);
    }


    /**
     * 绑定微信,注册平台账号
     *
     * @param localAuth
     * @return LocalAuthExecution
     * @throws LocalAuthOperationException
     */

    @Override
    @Transactional
    public LocalAuthExecution bindLocalAuthWithWechat(LocalAuth localAuth) throws LocalAuthOperationException {

        //判断要注册的平台号的对象是否为空,用户名密码和userId是否为空
        if (localAuth == null || localAuth.getUserName() == null || localAuth.getPassword() == null || localAuth.getUserId() == null) {
            return new LocalAuthExecution(LocalAuthStateEnum.NULL_AUTH_INFO);
        }
        try {
            //查询此用户平台号是否已经绑定微信用户
            LocalAuth tempAuth = localAuthMapper.selectLocalAuthByUserId(localAuth.getUserId());
            //查询用户名是否被使用
            LocalAuth validateUsername = localAuthMapper.localValidateUsername(localAuth.getUserName());
            if (tempAuth != null) {
                //如果绑定过退出,保证平台账号的唯一性
                return new LocalAuthExecution(LocalAuthStateEnum.ONLY_ONE_ACCOUNT);
            }
            if (validateUsername != null) {
                return new LocalAuthExecution(LocalAuthStateEnum.USER_NAME_ERROR);
            }
            //如果没绑定过微信号创建平台账号和该用户信息绑定
            localAuth.setCreateTime(new Date());
            localAuth.setLastEditTime(new Date());
            //密码加密
            localAuth.setPassword(MD5.getMd5(localAuth.getPassword()));

            int effectNum = localAuthMapper.registerLocalAuth(localAuth);
            if (effectNum <= 0) {
                throw new LocalAuthOperationException("绑定账号失败");
            } else {
                return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS, localAuth);
            }

        } catch (Exception e) {
            throw new LocalAuthOperationException("registerLocalAuth error:" + e.toString());
        }

    }

    /**
     * 修改平台账号的密码
     *
     * @param userId
     * @param username
     * @param password
     * @param newPassword
     * @return LocalAuthExecution
     * @throws LocalAuthOperationException
     */
    @Override
    @Transactional
    public LocalAuthExecution updateLocalAuthPwd(Integer userId, String username, String password, String newPassword) throws LocalAuthOperationException {

        //进行条件判断,账号中绑定的UserId,用户名密码以及新密码不能为空,新旧密码不能相同
        if (userId != null && username != null && password != null && newPassword != null) {
            try {

                //修改密码,传入加密的密码和数据库一致进行修改,设置新密码也要加密
                int effectNum = localAuthMapper.updateLocalAuth(userId, username, MD5.getMd5(password), MD5.getMd5(newPassword), new Date());

                if (effectNum <= 0) {
                    return new LocalAuthExecution(LocalAuthStateEnum.LOGINFAIL);
                } else {
                    return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new LocalAuthOperationException("updatePassword error:" + e.toString());
            }
        } else {
            return new LocalAuthExecution(LocalAuthStateEnum.NULL_AUTH_INFO);
        }
    }
}
