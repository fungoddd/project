package com.fun.service.impl;

import com.fun.dao.PersonInfoMapper;
import com.fun.entity.PersonInfo;
import com.fun.service.PersonInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: FunGod
 * @Date: 2018-12-08 01:02:05
 * @Desc: 用户操作业务层实现
 */
@Service
public class PersonInfoServiceImpl implements PersonInfoService {

    @Autowired
    private PersonInfoMapper personInfoMapper;


    @Override
    public PersonInfo getPersonInfoById(Integer userId) {
        return personInfoMapper.selectPersonInfoById(userId);
    }
}
