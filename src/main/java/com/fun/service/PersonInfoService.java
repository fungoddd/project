package com.fun.service;

import com.fun.entity.PersonInfo;

/**
 * @Author: FunGod
 * @Date: 2018-12-08 01:01:40
 * @Desc: 用户操作接口
 */
public interface PersonInfoService {
    /**
     * 通过用户id获取用户信息
     * @return
     */
    PersonInfo getPersonInfoById(Integer userId);

}
