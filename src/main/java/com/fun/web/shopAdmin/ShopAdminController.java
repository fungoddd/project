package com.fun.web.shopAdmin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author： FunGod
 * @Date: 2018-12-02 00:29:55
 * @Desc: 解析路由转发到对应的html中
 */

@Controller
@RequestMapping(value = "/shopAdmin", method = {RequestMethod.GET})
public class ShopAdminController {


    /**
     * 跳转到操作失败界面
     *
     * @return
     */
    @GetMapping("/operationFail")
    public String operationFail() {
        return "shop/operationFail";
    }

    /**
     * 跳转到操作成功界面
     *
     * @return
     */
    @GetMapping("/operationSuccess")
    public String operationSuccess() {
        return "shop/operationSuccess";
    }

    /**
     * 跳转到奖品管理
     *
     * @return
     */
    @GetMapping("/awardManagement")
    public String awardManagement() {
        return "shop/awardManagement";
    }

    /**
     * 跳转到奖品添加修改
     *
     * @return
     */
    @GetMapping("/awardOperation")
    public String awardOperation() {
        return "shop/addAwardOrUpdate";
    }

    /**
     * 跳转到消费记录
     *
     * @return
     */
    @GetMapping("/productBuyCheck")
    public String productBuyCheck() {
        return "shop/productBuyCheck";
    }

    /**
     * 跳转到奖品积分兑换
     *
     * @return
     */
    @GetMapping("/awardDeliverCheck")
    public String awardDeliverCheck() {
        return "shop/awardDeliverCheck";
    }

    /**
     * 跳转到顾客积分
     *
     * @return
     */
    @GetMapping("/userShopCheck")
    public String userShopCheck() {
        return "shop/userShopCheck";
    }

    /**
     * 跳转到授权管理的编辑页面
     */
    @GetMapping("/shopAuthEditPage")
    public String shopAuthEditPage() {
        return "shop/shopAuthEdit";
    }

    /**
     * 跳转到授权管理
     *
     * @return
     */
    @GetMapping("/shopAuthManagement")
    public String shopAuthManagement() {
        return "shop/shopAuthManagement";
    }

    /**
     * 跳转到商品管理
     */
    @GetMapping("/productManagement")
    public String productManagement() {
        return "shop/productManagement";
    }

    /**
     * 跳转到商品添加或修改
     */
    @GetMapping("/productOperation")
    public String productOperation() {
        return "shop/addProductOrUpdate";
    }

    /**
     * 跳转到商品类别管理界面
     */
    @GetMapping("/productCategoryManagement")
    public String productCategoryManagement() {
        return "shop/productCategoryManagement";
    }

    /**
     * 跳转到商店管理界面
     *
     * @return
     */
    @GetMapping("/shopManagement")
    public String shopManagement() {
        return "shop/shopManagement";
    }

    //跳转到商店注册或修改页面
    @GetMapping("/shopOperation")
    public String shopOperation() {
        return "shop/registShopOrUpdate";
    }

    /**
     * 跳转到商店列表页面
     *
     * @return
     */
    @GetMapping("/shopList")
    public String shopList() {
        return "shop/shoplist";
    }

}
