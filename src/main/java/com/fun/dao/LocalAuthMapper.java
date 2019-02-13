package com.fun.dao;

import com.fun.entity.LocalAuth;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @Authro: FunGod
 * @Date: 2018-12-09 14:51:01
 * @Desc: 平台账号Dao层
 */
public interface LocalAuthMapper {

    /**
     * 验证用户名是否已被使用
     * @param username
     * @return
     */
    LocalAuth localValidateUsername(@Param("username") String username);
    /**
     * 通过账号密码登录
     *
     * @param username
     * @param password
     * @return LocalAuth
     */
    LocalAuth localAuthLogin(@Param("username") String username, @Param("password") String password);

    /**
     * 通过平台账号中对应的userId获取查询指定用户
     *
     * @param userId
     * @return LocalAuth
     */
    LocalAuth selectLocalAuthByUserId(@Param("userId") Integer userId);

    /**
     * 注册平台账号
     *
     * @param localAuth
     * @return
     */
    int registerLocalAuth(LocalAuth localAuth);


    /**
     * 修改密码
     *
     * @param userId
     * @param username
     * @param password
     * @param newPassword
     * @param lastEditTime
     * @return
     */
    int updateLocalAuth(@Param("userId") Integer userId, @Param("username") String username, @Param("password") String password, @Param("newPassword") String newPassword, @Param("lastEditTime") Date lastEditTime);

}