package com.fun.web.front;

import com.fun.dto.userAwardMap.UserAwardMapExecution;
import com.fun.entity.*;
import com.fun.enums.UserAwardMapStateEnum;
import com.fun.service.AwardService;
import com.fun.service.UserAwardMapService;
import com.fun.service.UserShopMapService;
import com.fun.util.baidu.ShortNetAddress;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.verifycode.VerifyCodeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-18 18:44:43
 * @Desc: 我的奖品
 */
@Controller
@RequestMapping("/front")
public class MyAwardController {

    @Autowired
    private UserAwardMapService userAwardMapService;
    @Autowired
    private AwardService awardService;

    //微信获取用户信息的api前缀
    private static String urlPrefix;

    //微信获取用户信息的api中间部分
    private static String urlMiddle;

    //微信获取用户信息的api后缀
    private static String urlSuffix;

    //微信回传的响应的url
    private static String exchangeUrl;

    @Value("${wechat.prefix}")
    public void setUrlPrefix(String urlPrefix) {
        MyAwardController.urlPrefix = urlPrefix;
    }


    @Value("${wechat.middle}")
    public void setUrlMiddle(String urlMiddle) {
        MyAwardController.urlMiddle = urlMiddle;
    }

    @Value("${wechat.suffix}")
    public void setUrlSuffix(String urlSuffix) {
        MyAwardController.urlSuffix = urlSuffix;
    }

    @Value("${wechat.exchange.url}")
    public void setExchangeUrl(String exchangeUrl) {
        MyAwardController.exchangeUrl = exchangeUrl;
    }

    /**
     * 用户兑换奖品操作
     *
     * @param request
     * @return
     */
    @PostMapping("/addUserAwardMap")
    @ResponseBody
    private Map<String, Object> addUserAwardMap(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //session中获取用户信息
        PersonInfo user = (PersonInfo) request.getSession().getAttribute(USER);
        //获取请求的awardId
        Integer awardId = HttpServletRequestUtil.getInt(request, AWARD_ID);
        //获取当前shopId
        Integer shopId = HttpServletRequestUtil.getInt(request, SHOP_ID);
        //获取用户的积分
        Integer point = HttpServletRequestUtil.getInt(request, "point");
        if (user.getUserId() > 0 && awardId > 0 && shopId > 0 && point > 0) {
            //封装信息为用户和奖品映射对象
            UserAwardMap userAwardMap = new UserAwardMap();
            //绑定用户
            userAwardMap.setUser(user);
            //绑定奖品
            Award award = new Award();
            award.setAwardId(awardId);
            userAwardMap.setAward(award);
            //绑定所在商店
            Shop shop = new Shop();
            shop.setShopId(shopId);
            userAwardMap.setShop(shop);
            //绑定操作员(未领取奖品默认为自己)
            userAwardMap.setOperator(user);
            //添加积分
            userAwardMap.setPoint(point);

            //添加商品兑换信息
            try {
                UserAwardMapExecution uae = userAwardMapService.addUserAwardMap(userAwardMap);
                if (uae.getState() == UserAwardMapStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请选择领取的奖品");
        }
        return modelMap;
    }


    /**
     * 获取用户本人的兑换奖品记录列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getUserAwardMapListByUser")
    @ResponseBody
    private Map<String, Object> getUserAwardMapListByUser(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        PersonInfo user = (PersonInfo) request.getSession().getAttribute(USER);
        //获取前端传来的访问页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);

        if (user != null && user.getUserId() > 0 && pageNum >= 0 && pageSize >= 0) {
            UserAwardMap userAwardMap = new UserAwardMap();
            userAwardMap.setUser(user);
            //获取请求的ShopId,如果不为空则添加进查询条件
            Integer shopId = HttpServletRequestUtil.getInt(request, SHOP_ID);
            if (shopId > 0) {
                Shop shop = new Shop();
                shop.setShopId(shopId);
                userAwardMap.setShop(shop);
            }
            //添加查询条件
            String awardName = HttpServletRequestUtil.getString(request, AWARD_NAME);
            if (awardName != null && !awardName.equals("")) {
                Award award = new Award();
                award.setAwardName(awardName);
                userAwardMap.setAward(award);
            }
            PageHelper.startPage(pageNum, pageSize);
            List<UserAwardMap> userAwardMapList = userAwardMapService.getUserAwardMapList(userAwardMap);
            PageInfo info = new PageInfo(userAwardMapList);
            modelMap.put(SUCCESS, true);
            modelMap.put(USER_AWARD_MAP_LIST, userAwardMapList);
            modelMap.put(COUNT, info.getTotal());
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入正确条件查询");
        }
        return modelMap;
    }

    /**
     * 获取用户兑换奖品记录的详细信息
     *
     * @param request
     * @return
     */
    @GetMapping("/getUserAwardMapById")
    @ResponseBody
    private Map<String, Object> getUserAwardMapById(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        //获取前端传来的兑换记录的主键
        Integer userAwardId = HttpServletRequestUtil.getInt(request, USER_AWARD_ID);
        if (userAwardId > 0) {
            //获取顾客兑换奖品记录信息
            UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);
            //获取奖品信息
            Award award = awardService.getAwardById(userAwardMap.getAward().getAwardId());
            modelMap.put(SUCCESS, true);
            modelMap.put(AWARD, award);
            //返回是否领取信息
            modelMap.put(USED_STATUS, userAwardMap.getUsedStatus());
            modelMap.put(USER_AWARD_MAP, userAwardMap);

        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请选择正确兑换记录");
        }
        return modelMap;
    }

    /**
     * 生成带有URL的二维码,实现微信扫码跳转(奖品兑换二维码)
     */
    @GetMapping("/generateQRCode4award")
    @ResponseBody
    private void generateQRCode4award(HttpServletRequest request, HttpServletResponse response) {
        //获取前端传来的用户兑换奖品记录的Id
        Integer userAwardId = HttpServletRequestUtil.getInt(request, USER_AWARD_ID);
        //获取shopId
        Integer shopId = HttpServletRequestUtil.getInt(request, SHOP_ID);
        //通过奖品Id获取顾客兑换奖品的映射
        UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);
        //获取session中用户信息
        PersonInfo user = (PersonInfo) request.getSession().getAttribute(USER);
        if (userAwardMap != null && user != null && user.getUserId() != null && userAwardMap.getUser().getUserId() == user.getUserId()) {
            //获取当前时间戳,保证二维码时间有效性,精确到毫秒
            long timeStamp = System.currentTimeMillis();
            //将商店id和时间戳传入content,赋值到state中,微信获取到这些信息会回传到授权信息添加授权
            String content = "{aaauserAwardIdaaa:" + userAwardId + ",aaashopIdaaa:" + shopId + ",aaacustomerIdaaa:" + user.getUserId() + ",aaacreateTimeaaa:" + timeStamp + "}";
            try {
                //将content信息进行base64编码避免特殊字符干扰,之后拼接成目标URL
                String longUrl = urlPrefix + exchangeUrl + urlMiddle + URLEncoder.encode(content, "UTF-8")
                        + urlSuffix;
                //将目标url转为短url
                String shortUrl = ShortNetAddress.getShortURL(longUrl);
                //调用二维码工具类传入短URL,生成二维码
                BitMatrix QRCodeImg = VerifyCodeUtil.generatorQRCodeStream(shortUrl, response);
                //将二维码以图片流形式输出到前端
                MatrixToImageWriter.writeToStream(QRCodeImg, "png", response.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
