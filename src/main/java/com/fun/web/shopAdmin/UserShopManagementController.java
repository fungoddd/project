package com.fun.web.shopAdmin;

import com.fun.entity.PersonInfo;
import com.fun.entity.Shop;
import com.fun.entity.UserShopMap;
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

/**
 * @Author: FunGod
 * @Date: 2018-12-18 11:57:33
 * @Desc: 顾客和商店积分映射操作
 */
@Controller
@RequestMapping("/shopAdmin")
public class UserShopManagementController {

    @Autowired
    private UserShopMapService userShopMapService;

    /**
     * 获取用户在本店上的的积分兑换记录(已经领取的奖品)
     *
     * @param request
     * @return
     */
    @GetMapping("/getUserShopMapList")
    @ResponseBody
    private Map<String, Object> getUserShopMapList(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
        //获取前端传来的访问页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);

        if (curShop != null && curShop.getShopId() > 0 && pageNum >= 0 && pageSize >= 0) {
            //添加查询条件
            UserShopMap userShopMap = new UserShopMap();
            userShopMap.setShop(curShop);
            String username = HttpServletRequestUtil.getString(request, USERNAME);
            if (username != null && !username.equals("")) {
                PersonInfo user = new PersonInfo();
                user.setName(username);
                userShopMap.setUser(user);
            }
            PageHelper.startPage(pageNum, pageSize);
            List<UserShopMap> userShopMapList = userShopMapService.getUserShopMapList(userShopMap);
            PageInfo info = new PageInfo(userShopMapList);
            modelMap.put(SUCCESS, true);
            modelMap.put(USER_SHOP_MAP_LIST, userShopMapList);
            modelMap.put(COUNT, info.getPages());
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入正确条件查询");
        }
        return modelMap;
    }

}
