package com.fun.dao;

import com.fun.entity.Area;

import java.util.List;

public interface AreaMapper {
    /**
     *列出区域列表
     */
    List<Area>queryArea();

}