package com.fun.dao;

import com.fun.entity.PersonInfo;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 22:52:36
 * @Desc: 用户操作
 */
public interface PersonInfoMapper {
    /**
     * 通过id查询用户
     * @param userId
     * @return
     */
    PersonInfo selectPersonInfoById(Integer userId);

    /**
     * 添加用户
     *
     * @param personInfo
     * @return
     */
    int insertPerson(PersonInfo personInfo);


}