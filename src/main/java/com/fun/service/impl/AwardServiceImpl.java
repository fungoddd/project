package com.fun.service.impl;

import com.fun.dao.AwardMapper;
import com.fun.dto.award.AwardExecution;
import com.fun.entity.Award;
import com.fun.enums.AwardStateEnum;
import com.fun.exceptions.AwardOperationException;
import com.fun.service.AwardService;
import com.fun.util.Image.ImageHelper;
import com.fun.util.Image.ImageUtil;
import com.fun.util.Image.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 13:50:50
 * @Desc: 奖品管理业务层实现
 */
@Service
public class AwardServiceImpl implements AwardService {

    @Autowired
    private AwardMapper awardMapper;

    /**
     * 根据传入的查询条件获取奖品列表
     *
     * @param awardCondition
     * @return
     */
    @Override
    public List<Award> getAwardList(Award awardCondition) {
        if (awardCondition != null) {
            return awardMapper.selectAwardList(awardCondition);
        } else {
            return null;
        }
    }

    /**
     * 通过主键获取奖品信息
     *
     * @param awardId
     * @return
     */
    @Override
    public Award getAwardById(Integer awardId) {
        if (awardId > 0) {
            return awardMapper.selectAwardById(awardId);
        } else {
            return null;
        }
    }

    /**
     * 添加奖品信息包括图片
     *
     * @param award
     * @param thumbnail
     * @return
     * @throws AwardOperationException
     */
    @Override
    @Transactional
    public AwardExecution addAward(Award award, ImageHelper thumbnail) throws AwardOperationException {
        if (award != null && award.getShopId() > 0) {
            award.setCreateTime(new Date());
            award.setLastEditTime(new Date());
            award.setEnableStatus(1);
            try {
                if (thumbnail != null) {
                    addThumbnail(award, thumbnail);
                }
                int effectNum = awardMapper.insertAward(award);
                if (effectNum <= 0) {
                    throw new AwardOperationException("添加奖品信息失败");
                } else {
                    return new AwardExecution(AwardStateEnum.SUCCESS, award);
                }
            } catch (Exception e) {
                throw new AwardOperationException("addAward error:" + e.toString());
            }
        } else {
            return new AwardExecution(AwardStateEnum.EMPTY);
        }
    }

    /**
     * 修改奖品信息,包括图片
     *
     * @param award
     * @param thumbnail
     * @return
     * @throws AwardOperationException
     */
    @Override
    @Transactional
    public AwardExecution updateAward(Award award, ImageHelper thumbnail) throws AwardOperationException {
        if (award != null && award.getAwardId() != null) {
            award.setLastEditTime(new Date());
            //如果上传商品缩略图不为空并且原有缩略图不为空则删除原有缩略图添加
            if (thumbnail != null) {
                //先获取数据库中该商品信息,获取缩略图地址进行删除
                Award tempAward = awardMapper.selectAwardById(award.getAwardId());
                if (tempAward.getAwardImg() != null) {
                    ImageUtil.deleteFileOrPath(tempAward.getAwardImg());
                }
                //添加奖品缩略图
                addThumbnail(award, thumbnail);
            }
            try {
                int effectNum = awardMapper.updateAward(award);
                if (effectNum <= 0) {
                    throw new AwardOperationException("修改商品信息失败");
                } else {
                    return new AwardExecution(AwardStateEnum.SUCCESS, award);
                }
            } catch (Exception e) {
                throw new AwardOperationException("updateAward error:" + e.toString());
            }
        } else {
            return new AwardExecution(AwardStateEnum.EMPTY);
        }
    }

    /**
     * 添加商品缩略图
     *
     * @param award
     * @param thumbnail
     */
    private void addThumbnail(Award award, ImageHelper thumbnail) {

        try {
            //获得商品下的缩略图子路径
            String relativeImgPath = PathUtil.getShopImgPath(award.getShopId());

            //图片真实路径,传入图片文件流和图片子路径
            String thumbnailPath = ImageUtil.generateThumbnail(thumbnail, relativeImgPath);

            //商品设置图片
            award.setAwardImg(thumbnailPath);

        } catch (Exception e) {

            throw new AwardOperationException("添加缩略图失败!" + e.toString());
        }
    }

    /**
     * 删除奖品信息通过奖品id和商店id
     *
     * @param awardId
     * @param shopId
     * @return
     * @throws AwardOperationException
     */
    @Override
    @Transactional
    public AwardExecution deleteAwardByAwardIdAndShopId(Integer awardId, Integer shopId) throws AwardOperationException {
        if (awardId > 0 && shopId > 0) {
            try {
                //先获取数据库中该商品信息,获取缩略图地址进行删除
                Award tempAward = awardMapper.selectAwardById(awardId);
                if (tempAward.getAwardImg() != null) {
                    ImageUtil.deleteFileOrPath(tempAward.getAwardImg());
                }
                int effectNum = awardMapper.deleteAward(awardId, shopId);
                if (effectNum <= 0) {
                    throw new AwardOperationException("删除奖品信息失败");
                } else {
                    return new AwardExecution(AwardStateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new AwardOperationException("deleteAward error:" + e.toString());
            }
        } else {
            return new AwardExecution(AwardStateEnum.INNER_ERROR);
        }
    }
}
