package com.fun.service.impl;

import com.fun.dao.PersonInfoMapper;
import com.fun.dao.WechatAuthMapper;
import com.fun.dto.wechat.WechatAuthExecution;
import com.fun.entity.PersonInfo;
import com.fun.entity.WechatAuth;
import com.fun.enums.WechatAuthStateEnum;
import com.fun.exceptions.WechatAuthOpeartionException;
import com.fun.service.WechatAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author: FunGod
 * @Date: 2018-12-08 01:18:20
 * @Desc: 微信用户业务层实现
 */
@Service
public class WechatAuthServiceImpl implements WechatAuthService {

    private static Logger logger = LoggerFactory.getLogger(WechatAuthServiceImpl.class);

    @Autowired
    private WechatAuthMapper wechatAuthMapper;
    @Autowired
    private PersonInfoMapper personInfoMapper;

    /**
     * 用户注册微信账号
     *
     * @param wechatAuth
     * @return
     */
    @Transactional
    @Override
    public WechatAuthExecution register(WechatAuth wechatAuth) throws WechatAuthOpeartionException {
        //如果微信号不为空并且携带openId进行创建
        if (wechatAuth == null || wechatAuth.getOpenId() == null) {
            return new WechatAuthExecution(WechatAuthStateEnum.NULL_AUTH_INFO);
        }
        try {
            //在本服务号的微信号注册时间
            wechatAuth.setCreateTime(new Date());
            //如果微信号中用户信息不为空,且不携带用户id,默认第一次使用本服务号,自动注册微信信息和用户信息
            if (wechatAuth.getPersonInfo() != null && wechatAuth.getPersonInfo().getUserId() == null) {
                try {
                    //默认状态为1可用
                    wechatAuth.getPersonInfo().setEnableStatus(1);
                    wechatAuth.getPersonInfo().setCreateTime(new Date());
                    wechatAuth.getPersonInfo().setLastEditTime(new Date());

                    PersonInfo personInfo = wechatAuth.getPersonInfo();
                    //注册用户信息
                    int effectNum = personInfoMapper.insertPerson(personInfo);

                    if (effectNum <= 0) {
                        throw new WechatAuthOpeartionException("注册用户信息失败");
                    }
                } catch (Exception e) {
                    logger.error("insertPersonInfo error:" + e.toString());
                    throw new WechatAuthOpeartionException("insertPersonInfo error:" + e.toString());
                }
            }
            //注册本服务号的用户微信账号信息
            int effectNum = wechatAuthMapper.insertWechatUser(wechatAuth);

            if (effectNum <= 0) {
                throw new WechatAuthOpeartionException("微信账号注册失败");

            } else {
                return new WechatAuthExecution(WechatAuthStateEnum.SUCCESS, wechatAuth);
            }

        } catch (Exception e) {
            logger.error("insertWeChatUser error" + e.toString());
            throw new WechatAuthOpeartionException("insertWeChatUser error" + e.toString());
        }
    }

    /**
     * 查询微信用户
     *
     * @param openId
     * @return WechatAuth
     */
    @Override
    public WechatAuth getWechatUserByOpenId(String openId) {
        return wechatAuthMapper.selectWechatUserInfoByOpenId(openId);
    }
}
