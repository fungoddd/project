package com.fun.service;

import com.fun.dto.award.AwardExecution;
import com.fun.entity.Award;
import com.fun.exceptions.AwardOperationException;
import com.fun.util.Image.ImageHelper;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 13:41:14
 * @Desc: 奖品管理接口
 */
public interface AwardService {
    /**
     * 根据传入的查询条件获取奖品列表
     *
     * @param awardCondition
     * @return
     */
    List<Award> getAwardList(Award awardCondition);

    /**
     * 通过主键获取奖品信息
     *
     * @param awardId
     * @return
     */
    Award getAwardById(Integer awardId);


    /**
     * 添加奖品信息包括图片
     *
     * @param award
     * @param thumbnail
     * @return
     * @throws AwardOperationException
     */
    AwardExecution addAward(Award award, ImageHelper thumbnail) throws AwardOperationException;

    /**
     * 修改奖品信息,包括图片
     *
     * @param award
     * @param thumbnail
     * @return
     * @throws AwardOperationException
     */
    AwardExecution updateAward(Award award, ImageHelper thumbnail) throws AwardOperationException;

    /**
     * 删除奖品信息通过奖品id和商店id
     *
     * @param awardId
     * @param shopId
     * @return
     * @throws AwardOperationException
     */
    AwardExecution deleteAwardByAwardIdAndShopId(Integer awardId, Integer shopId) throws AwardOperationException;

}
