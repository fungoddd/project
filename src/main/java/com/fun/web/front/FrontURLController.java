package com.fun.web.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Authro: FunGod
 * @Date: 2018-12-02 20:04:35
 * @Desc: 首页展示的路由
 */
@Controller
@RequestMapping(value = "/front", method = RequestMethod.GET)
public class FrontURLController {


    /**
     * 跳转到奖品详情
     *
     * @return
     */
    @GetMapping("/myAwardDetail")
    public String myAwardDetail() {
        return "front/awardDetail";
    }
    /**
     * 跳转到奖品兑换
     *
     * @return
     */
    @GetMapping("/pointRecord")
    public String pointRecord() {
        return "front/pointRecord";
    }

    /**
     * 跳转到商店下的奖品列表信息页面
     *
     * @return
     */
    @GetMapping("/awardList")
    public String awardList() {
        return "front/awardList";
    }

    /**
     * 跳转到商店下的商品信息页面
     *
     * @return
     */
    @GetMapping("/productListDetail")
    public String productListDetail() {
        return "front/productDetail";
    }

    /**
     * 跳转到商店下的详情信息页面
     *
     * @return
     */
    @GetMapping("/frontShopDetail")
    public String frontShopDetail() {
        return "front/shopDetail";
    }

    /**
     * 跳转到前端展示的商店列表
     *
     * @return
     */
    @GetMapping("/frontShopList")
    public String frontShopListInfo() {
        return "front/shopList";
    }

    /**
     * 跳转到首页
     *
     * @return
     */
    @GetMapping(value = "/index")
    private String index() {
        return "front/index";
    }
}
