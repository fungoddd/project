package com.fun.interceptor.shopAdmin;


import com.fun.entity.Shop;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.fun.constant.ControllerConst.CURRENT_SHOP;
import static com.fun.constant.ControllerConst.SHOP_LIST;

/**
 * @Author: FunGod
 * @Date: 2018-12-10 01:32:43
 * @Desc: 店家管理系统验证拦截器
 */
public class ShopPermissionInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //session中获取当前选择的店铺
        Shop currentShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);

        //session中获取当前用户可操作的商店列表
        //@SuppressWarnings("unchecked")
        List<Shop> shopList = (List<Shop>) request.getSession().getAttribute(SHOP_LIST);
        //如果当前店铺信息不为空商店可操作列表不为空,对可操作的店铺列表进行遍历
        if (currentShop != null && shopList != null) {
            for (Shop shop : shopList) {
                //当前店铺在可操作的列表里返回true,用户可以继续操作
                if (shop.getShopId() == currentShop.getShopId()) {
                    return true;
                }
            }
        }
        //不满足拦截条件终止用户操作
        return false;
    }
}
