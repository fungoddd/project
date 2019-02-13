package com.fun.web.front;

import com.fun.entity.Award;
import com.fun.entity.PersonInfo;
import com.fun.entity.UserShopMap;
import com.fun.service.AwardService;
import com.fun.service.UserShopMapService;
import com.fun.util.request.HttpServletRequestUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;
import static com.fun.constant.ControllerConst.ERR_MSG;
import static com.fun.constant.ControllerConst.SUCCESS;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 18:36:49
 * @Desc: 用户在指定商店下对奖品操作
 */
@Controller
@RequestMapping("/front")
public class ShopAwardController {
    @Autowired
    private AwardService awardService;
    @Autowired
    private UserShopMapService userShopMapService;

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
        //获取商店Id
        Integer shopId = HttpServletRequestUtil.getInt(request, SHOP_ID);
        //获取前端传来的访问页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);

        if (shopId > 0 && pageNum >= 0 && pageSize >= 0) {
            //判断查询条件是否包含奖品名
            String awardName = HttpServletRequestUtil.getString(request, AWARD_NAME);
            //拼接查询条件
            Award awardCondition = compactAwardCondition(shopId, awardName);
            PageHelper.startPage(pageNum, pageSize);
            List<Award> awardList = awardService.getAwardList(awardCondition);
            PageInfo info = new PageInfo(awardList);
            modelMap.put(SUCCESS, true);
            modelMap.put(COUNT, info.getTotal());
            modelMap.put(AWARD_LIST, awardList);
            //session中获取用户信息,显示该用户在当前商店积分
            PersonInfo user = (PersonInfo) request.getSession().getAttribute(USER);
            if (user != null && user.getUserId() > 0) {
                //获取该用户在本店积分
                UserShopMap userShopMap = userShopMapService.getUserShopMapById(user.getUserId(), shopId);
                //如果没有该信息积分显示0否则显示对应积分
                if (userShopMap == null) {
                    modelMap.put("totalPoint", 0);
                } else {
                    modelMap.put("totalPoint", userShopMap.getPoint());
                }
            }
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
        awardCondition.setEnableStatus(1);
        // 若有商品模糊查询的要求则添加进去
        if (awardName != null && !awardName.equals("")) {
            awardCondition.setAwardName(awardName);
        }
        return awardCondition;
    }
}
