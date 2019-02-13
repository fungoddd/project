package com.fun.service;

import com.fun.entity.Area;

import java.util.List;

public interface AreaService {

    //定义redis的key前缀
    public static final String AREA_LIST_KEY = "arealist";

    /**
     * 获取区域列表,先从redis缓存中取
     *
     * @return
     */
    List<Area> getAreaList();
}
