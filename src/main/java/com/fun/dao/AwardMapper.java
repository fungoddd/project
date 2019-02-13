package com.fun.dao;

import com.fun.entity.Award;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-12 19:29:14
 * @Desc: 奖品管理dao开发
 */
public interface AwardMapper {

    /**
     * 获取奖品列表,分页显示(奖品名字,商店id,奖品可用状态查询)
     * @param award
     * @return
     */
    List<Award>selectAwardList(Award award);

    /**
     * 通过奖品id获取奖品信息
     * @param awardId
     * @return
     */
    Award selectAwardById(Integer awardId);

    /**
     * 添加奖品
     * @param award
     * @return
     */
    int insertAward(Award award);

    /**
     * 通过奖品主键和商店主键删除指定奖品
     * @param awardId
     * @param shopId
     * @return
     */
    int deleteAward(@Param("awardId") Integer awardId,@Param("shopId")Integer shopId);

    /**
     * 修改奖品信息
     * @param award
     * @return
     */
    int updateAward(Award award);

}