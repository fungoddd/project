package com.fun.dao;

import com.fun.entity.HeadLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HeadLineMapper {

    /**
     * 根据传入的查询条件（头条名）查询头条
     * @param headLineCondition
     *
     * @return
     */
    List<HeadLine> selectHeadLine(@Param("headLineCondition") HeadLine headLineCondition);


}