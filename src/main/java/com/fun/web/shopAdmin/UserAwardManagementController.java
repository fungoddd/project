package com.fun.web.shopAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.dto.shopAuthMap.ShopAuthMapExecution;
import com.fun.dto.userAwardMap.UserAwardMapExecution;
import com.fun.dto.userProductMap.UserProductMapExecution;
import com.fun.dto.wechat.UserAccessToken;
import com.fun.dto.wechat.WeChatInfo;
import com.fun.entity.*;
import com.fun.enums.UserAwardMapStateEnum;
import com.fun.enums.UserProductMapStateEnum;
import com.fun.service.ShopAuthMapService;
import com.fun.service.UserAwardMapService;
import com.fun.service.WechatAuthService;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.wechat.WeChatUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 12:45:26
 * @Desc: 用户领取奖品操作
 */
@Controller
@RequestMapping("/shopAdmin")
public class UserAwardManagementController {

    @Autowired
    private UserAwardMapService userAwardMapService;
    @Autowired
    private WechatAuthService wechatAuthService;
    @Autowired
    private ShopAuthMapService shopAuthMapService;


    /**
     * 获取用户的领取奖品的信息列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getUserAwardMapList")
    @ResponseBody
    private Map<String, Object> getUserAwardMapList(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
        //获取前端传来的访问页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);

        if (curShop != null && curShop.getShopId() > 0 && pageNum >= 0 && pageSize >= 0) {
            //添加查询条件
            UserAwardMap userAwardMap = new UserAwardMap();
            userAwardMap.setShop(curShop);
            String awardName = HttpServletRequestUtil.getString(request, AWARD_NAME);
            if (awardName != null && !awardName.equals("")) {
                Award award = new Award();
                award.setAwardName(awardName);
                userAwardMap.setAward(award);
            }
            //后台显示已经领取奖品的记录(操作员操作过的)
            userAwardMap.setUsedStatus(1);
            PageHelper.startPage(pageNum, pageSize);
            List<UserAwardMap> userAwardMapList = userAwardMapService.getUserAwardMapList(userAwardMap);
            PageInfo info = new PageInfo(userAwardMapList);
            modelMap.put(SUCCESS, true);
            modelMap.put(USER_AWARD_MAP_LIST, userAwardMapList);
            modelMap.put(COUNT, info.getPages());
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入正确条件查询");
        }
        return modelMap;
    }

    /**
     * 修改奖品领取状态
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping("/exchangeAward")
    private String exchangeAward(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //从请求域request里获取微信用户(员工)信息
        WechatAuth auth = getEmployeeInfo(request);
        if (auth != null) {
            //通过微信用户中的用户id获取用户信息
            PersonInfo operator = auth.getPersonInfo();
            //将用户信息添加到session
            request.getSession().setAttribute(USER, operator);
            //解析微信回传过来的自定义参数state,上面进行了编码需要解码
            String QRCodeInfo = new String(URLDecoder.decode(HttpServletRequestUtil.
                    getString(request, "state"), "UTF-8"));
            ObjectMapper objectMapper = new ObjectMapper();
            WeChatInfo weChatInfo = null;
            try {
                //解码后的内容要把之前生成二维码加入的aaa前缀替换掉,再转成WeChatInfo实体类
                weChatInfo = objectMapper.readValue(QRCodeInfo.replace("aaa", "\""), WeChatInfo.class);

            } catch (Exception e) {
                return "shopAdmin/operationFail";
            }
            //校验验证码是否获取,超过十分钟跳转到失败页面
            if (!checkQRCodeInfo(weChatInfo)) {
                return "shop/operationFail";
            }
            //获取用户兑换奖品映射主键
            Integer userAwardId = weChatInfo.getUserAwardId();
            //获取顾客id
            Integer customerId = weChatInfo.getCustomerId();
            //顾客信息,操作员信息,用户兑换奖品映射信息封装
            UserAwardMap userAwardMap = compactExchangeAward(customerId, userAwardId, operator.getUserId());
            //绑定微信实体类获取到的shopId
            userAwardMap.setShopId(weChatInfo.getShopId());
            if (userAwardMap != null && customerId > 0) {
                try {
                    //校验当前操作的人是否是本店员工且有权限
                    if (!checkShopAuth(operator.getUserId(), userAwardMap)) {
                        return "shop/operationFail";
                    }
                    //修改奖品领取状态
                    UserAwardMapExecution uae = userAwardMapService.updateUserAwardMap(userAwardMap);
                    if (uae.getState() == UserAwardMapStateEnum.SUCCESS.getState()) {
                        return "shop/operationSuccess";
                    }
                } catch (Exception e) {
                    return "shop/operationFail";
                }
            }
        }
        return "shop/operationFail";
    }


    /**
     * 封装用户领取奖品信息(操作员操作)所需参数
     *
     * @param customerId
     * @param userAwardId
     * @param operatorId
     * @return
     */
    private UserAwardMap compactExchangeAward(Integer customerId, Integer userAwardId, Integer operatorId) {
        UserAwardMap userAwardMap = new UserAwardMap();
        if (customerId > 0 && userAwardId > 0 && operatorId > 0) {
            PersonInfo user = new PersonInfo();
            user.setUserId(customerId);

            userAwardMap.setUser(user);
            userAwardMap.setOperatorId(operatorId);
            userAwardMap.setUserAwardId(userAwardId);
            userAwardMap.setUsedStatus(1);

            return userAwardMap;
        }
        return null;
    }

    /**
     * 校验操作员是否有权限
     *
     * @param userId
     * @param userAwardMap
     * @return
     */
    private boolean checkShopAuth(Integer userId, UserAwardMap userAwardMap) {
        //查询该商店下授权信息
        ShopAuthMapExecution sae = shopAuthMapService.getShopAuthMapListByShopId(userAwardMap.getShopId());
        //遍历授权信息,匹配当前操作员的id和当前商店授权信息的员工id一致并且状态可用返回true
        for (ShopAuthMap shopAuthMap : sae.getShopAuthMapList()) {
            if (shopAuthMap.getEmployee().getUserId() == userId && shopAuthMap.getEnableStatus() != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过微信回传的code获取用户信息
     *
     * @param request
     * @return
     */
    private WechatAuth getEmployeeInfo(HttpServletRequest request) {
        String code = request.getParameter(CODE);
        WechatAuth auth = null;
        if (code != null) {
            UserAccessToken token;
            try {//通过用户的code跟服务器换取token
                token = WeChatUtil.getUserAccessToken(code);
                String openId = token.getOpenId();
                request.getSession().setAttribute(OPEN_ID, openId);
                //通过token携带的openId查询数据库获取微信用户信息
                auth = wechatAuthService.getWechatUserByOpenId(openId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return auth;
    }

    /**
     * 检验二维码是否过期
     *
     * @param weChatInfo
     * @return
     */
    private boolean checkQRCodeInfo(WeChatInfo weChatInfo) {
        if (weChatInfo != null && weChatInfo.getUserAwardId() != null
                && weChatInfo.getCustomerId() != null && weChatInfo.getCreateTime() != null
                && weChatInfo.getShopId() != null) {
            Long curTime = System.currentTimeMillis();
            //判断是否小于十分钟
            if (curTime - weChatInfo.getCreateTime() <= 600000) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
