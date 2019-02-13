package com.fun.web.shopAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.dto.award.AwardExecution;
import com.fun.entity.Award;
import com.fun.entity.Shop;
import com.fun.enums.AwardStateEnum;
import com.fun.exceptions.ProductOperationException;
import com.fun.service.AwardService;
import com.fun.util.Image.ImageHelper;
import com.fun.util.Image.ImageUtil;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.verifycode.VerifyCodeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.management.loading.MLet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;
import static com.fun.constant.ControllerConst.ERR_MSG;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 14:14:52
 * @Desc: 奖品管理操作
 */
@Controller
@RequestMapping("/shopAdmin")
public class AwardManagementController {
    @Autowired
    private AwardService awardService;

    /**
     * 获取当前商店下奖品信息列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getAwardList")
    @ResponseBody
    private Map<String, Object> getAwardList(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
        //获取前端传来的访问页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);

        if (curShop != null && curShop.getShopId() > 0 && pageNum >= 0 && pageSize >= 0) {
            //判断查询条件是否包含奖品名
            String awardName = HttpServletRequestUtil.getString(request, AWARD_NAME);
            //拼接查询条件
            Award awardCondition = compactAwardCondition(curShop.getShopId(), awardName);
            PageHelper.startPage(pageNum, pageSize);
            List<Award> awardList = awardService.getAwardList(awardCondition);
            PageInfo info = new PageInfo(awardList);
            modelMap.put(SUCCESS, true);
            modelMap.put(COUNT, info.getPages());
            modelMap.put(AWARD_LIST, awardList);
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入正确条件查询");
        }
        return modelMap;
    }

    /**
     * 获取当前查询条件:查询奖品
     *
     * @param shopId
     * @param awardName
     * @return
     */
    private Award compactAwardCondition(Integer shopId, String awardName) {

        Award awardCondition = new Award();
        //把session中的当前shop对象的Id添加到奖品对象中,指定查询该商店下的奖品
        awardCondition.setShopId(shopId);
        // 若有商品模糊查询的要求则添加进去
        if (awardName != null && !awardName.equals("")) {
            awardCondition.setAwardName(awardName);
        }
        return awardCondition;
    }

    /**
     * 获取指定商店下的指定奖品信息
     *
     * @param request
     * @return
     */
    @GetMapping("/getAwardById")
    @ResponseBody
    private Map<String, Object> getAwardById(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        Integer awardId = HttpServletRequestUtil.getInt(request, AWARD_ID);
        if (awardId > 0) {
            Award award = awardService.getAwardById(awardId);
            modelMap.put(SUCCESS, true);
            modelMap.put(AWARD, award);
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "奖品Id不能为空");
        }
        return modelMap;
    }

    /**
     * 添加奖品
     *
     * @param request
     * @return
     */
    @PostMapping("/addAward")
    @ResponseBody
    private Map<String, Object> addAward(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //验证码校验
        if (!VerifyCodeUtil.checkVerifyCode(request)) {

            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误!");
            return modelMap;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Award award = null;
        ImageHelper thumbnail = null;
        String awardStr = HttpServletRequestUtil.getString(request, AWARD_STR);
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver
                (request.getSession().getServletContext());
        try {
            //如果前端传的文件具备Multipart文件流进行强制转换
            if (multipartResolver.isMultipart(request)) {
                MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                thumbnail = getImageHolder(multipartHttpServletRequest, thumbnail);
            }
        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "只能上传图片");
            return modelMap;
        }
        try {

            award = objectMapper.readValue(awardStr, Award.class);
        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }
        if (award != null && thumbnail != null) {
            try {
                Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
                award.setShopId(curShop.getShopId());
                AwardExecution ae = awardService.addAward(award, thumbnail);
                if (ae.getState() == AwardStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);
                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, ae.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "奖品信息不能为空");
        }
        return modelMap;

    }

    /**
     * 修改奖品信息
     *
     * @param request
     * @return
     */
    @PostMapping("/updateAward")
    @ResponseBody
    private Map<String, Object> updateAward(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        //如果是修改操作进行验证码验证,其他操作不验证
        boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");

        //验证码校验
        if (!statusChange && !VerifyCodeUtil.checkVerifyCode(request)) {

            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误!");
            return modelMap;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Award award = null;
        ImageHelper thumbnail = null;
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver
                (request.getSession().getServletContext());
        try {
            //如果前端传的文件具备Multipart文件流进行强制转换
            if (multipartResolver.isMultipart(request)) {
                MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                thumbnail = getImageHolder(multipartHttpServletRequest, thumbnail);
            }
        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "只能上传图片");
            return modelMap;
        }
        try {
            String awardStr = HttpServletRequestUtil.getString(request, AWARD_STR);
            award = objectMapper.readValue(awardStr, Award.class);
        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }
        if (award != null) {
            try {
                Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
                award.setShopId(curShop.getShopId());
                AwardExecution ae = awardService.updateAward(award, thumbnail);
                if (ae.getState() == AwardStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);
                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, ae.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "奖品信息不能为空");
        }
        return modelMap;
    }

    /**
     * 删除指定商店下的指定奖品信息
     *
     * @param request
     * @return
     */
    @PostMapping("/deleteAward")
    @ResponseBody
    private Map<String, Object> deleteAward(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //获取session中的shop对象
        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
        Integer awardId = HttpServletRequestUtil.getInt(request, AWARD_ID);

        if (curShop != null && curShop.getShopId() > 0 && awardId > 0) {
            try {
                AwardExecution ae = awardService.deleteAwardByAwardIdAndShopId(awardId, curShop.getShopId());
                if (ae.getState() == AwardStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请登陆后指定奖品在操作");
        }
        return modelMap;
    }

    /**
     * 缩略图处理
     *
     * @param request
     * @param thumbnail
     * @return
     * @throws IOException
     */
    private ImageHelper getImageHolder(MultipartHttpServletRequest request, ImageHelper thumbnail) throws IOException {

        MultipartHttpServletRequest multipartHttpServletRequest = request;
        // 取出缩略图并构建imageHelper对象
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("thumbnail");

        if (thumbnailFile != null) {
            //验证是否为图片
            if (!ImageUtil.isImg(thumbnailFile.getInputStream())) {

                throw new ProductOperationException("只能上传图片!");
            }

            thumbnail = new ImageHelper(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        }

        //返回处理后的缩略图
        return thumbnail;
    }

}
