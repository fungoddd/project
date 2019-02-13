package com.fun.service;

import com.fun.entity.HeadLine;

import java.util.List;

public interface HeadLineService {
    //redis的前缀key
    public static final String HEADLINE_LIST_KEY = "headlinelist";
    /**
     * 查询头条列表,优先从redis缓存中取
     * @param headLine
     * @return
     */
    List<HeadLine> getHeadLineList(HeadLine headLine);
}
