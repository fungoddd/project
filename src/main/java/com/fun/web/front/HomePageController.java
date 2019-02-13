package com.fun.web.front;

import com.fun.entity.HeadLine;
import com.fun.entity.ShopCategory;
import com.fun.service.AreaService;
import com.fun.service.HeadLineService;
import com.fun.service.ShopCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-02 19:45:49
 * @Desc: 前端首页
 */
@Controller
@RequestMapping("/front")
public class HomePageController {

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Autowired
    private HeadLineService headLineService;

    @Autowired
    private AreaService areaService;


    /**
     * 初始前端展示主页信息(获取一级商店类别列表及头条列表)
     *
     * @return
     */
    @GetMapping("/homePageInfo")
    @ResponseBody
    private Map<String, Object> homePageInfo() {

        Map<String, Object> modelMap = new HashMap<>();

        List<ShopCategory> shopCategoryList = new ArrayList<>();
        List<HeadLine> headLineList = new ArrayList<>();

        try {
            //获取一级商店类别列表(parentId=null的商品类别 )
            shopCategoryList = shopCategoryService.getShopCategoryList(null);

            //获取头条状态可用的(为1)的头条列表
            HeadLine headLine = new HeadLine();
            headLine.setEnableStatus(1);
            headLineList = headLineService.getHeadLineList(headLine);

            modelMap.put(SUCCESS, true);
            modelMap.put(SHOP_CATEGORY_LIST, shopCategoryList);
            modelMap.put(HEAD_LINE_LIST, headLineList);

        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }
        return modelMap;
    }
}
