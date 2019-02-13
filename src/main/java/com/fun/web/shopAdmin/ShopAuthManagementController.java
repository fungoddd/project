package com.fun.web.shopAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.dto.shopAuthMap.ShopAuthMapExecution;
import com.fun.dto.wechat.UserAccessToken;
import com.fun.dto.wechat.WeChatInfo;
import com.fun.dto.wechat.WeChatUser;
import com.fun.entity.PersonInfo;
import com.fun.entity.Shop;
import com.fun.entity.ShopAuthMap;
import com.fun.entity.WechatAuth;
import com.fun.enums.ShopAuthMapStateEnum;
import com.fun.service.PersonInfoService;
import com.fun.service.ShopAuthMapService;
import com.fun.service.ShopService;
import com.fun.service.WechatAuthService;
import com.fun.util.baidu.ShortNetAddress;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.verifycode.VerifyCodeUtil;
import com.fun.util.wechat.WeChatUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-13 19:33:14
 * @Desc: 商店员工授权管理
 */
@Controller
@RequestMapping("/shopAdmin")
public class ShopAuthManagementController {

    @Autowired
    private ShopAuthMapService shopAuthMapService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private WechatAuthService wechatAuthService;

    @Autowired
    private ShopService shopService;

    //微信获取用户信息的api前缀
    private static String urlPrefix;

    //微信获取用户信息的api中间部分
    private static String urlMiddle;

    //微信获取用户信息的api后缀
    private static String urlSuffix;

    //微信回传的响应添加授权信息的url
    private static String addShopAuthUrl;

    @Value("${wechat.prefix}")
    public void setUrlPrefix(String urlPrefix) {
        ShopAuthManagementController.urlPrefix = urlPrefix;
    }


    @Value("${wechat.middle}")
    public void setUrlMiddle(String urlMiddle) {
        ShopAuthManagementController.urlMiddle = urlMiddle;
    }

    @Value("${wechat.suffix}")
    public void setUrlSuffix(String urlSuffix) {
        ShopAuthManagementController.urlSuffix = urlSuffix;
    }

    @Value("${wechat.addShopAuth.url}")
    public void setAddShopAuthUrl(String addShopAuthUrl) {
        ShopAuthManagementController.addShopAuthUrl = addShopAuthUrl;
    }

    /**
     * 生成带有URL的二维码,实现微信扫码跳转
     */
    @GetMapping("/generateQRCode4shopAuth")
    @ResponseBody
    private void generateQRCode4shopAuth(HttpServletRequest request, HttpServletResponse response) {
        //session中获取shop的信息
        Shop shop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
        if (shop != null && shop.getShopId() != null) {
            //获取当前时间戳,保证二维码时间有效性,精确到毫秒
            long timeStamp = System.currentTimeMillis();
            //将商店id和时间戳传入content,赋值到state中,微信获取到这些信息会回传到授权信息添加授权
            String content = "{aaashopIdaaa:" + shop.getShopId() + ",aaacreateTimeaaa:" + timeStamp + "}";
            try {
                //将content信息进行base64编码避免特殊字符干扰,之后拼接成目标URL
                String longUrl = urlPrefix + addShopAuthUrl + urlMiddle + URLEncoder.encode(content, "UTF-8")
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

    /**
     * 通过微信回传来的参数添加商店员工授权信息
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/addShopAuthMap")
    private String addShopAuthMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //从请求域request里获取微信用户(员工)信息
        WechatAuth auth = getEmployeeInfo(request);
        if (auth != null) {
            //通过微信用户中的用户id获取用户信息
            PersonInfo user = personInfoService.getPersonInfoById(auth.getPersonInfo().getUserId());
            //将用户信息添加到session
            request.getSession().setAttribute(USER, user);
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
            //去重校验,放置重复扫码添加重复信息
            ShopAuthMapExecution allShopAuthList = shopAuthMapService.getShopAuthMapListByShopId(weChatInfo.getShopId());
            List<ShopAuthMap> shopAuthMapList = allShopAuthList.getShopAuthMapList();
            for (ShopAuthMap shopAuthMap : shopAuthMapList) {
                if (shopAuthMap.getEmployee().getUserId() == user.getUserId()) {
                    return "shop/operationFail";
                }
            }
            try {
                //通过获取到的内容,添加微信授权信息
                ShopAuthMap shopAuthMap = new ShopAuthMap();
                //绑定所在商店
                Shop shop = new Shop();
                shop.setShopId(weChatInfo.getShopId());
                shopAuthMap.setShop(shop);
                //添加员工信息
                shopAuthMap.setEmployee(user);
                //获取session中的user
                //PersonInfo master = (PersonInfo) request.getSession().getAttribute(USER);
                //如果当前添加的用户信息和session中登录的用户信息是同一个说明是本店主人
                /*if (user.getUserId() == master.getUserId()) {
                    shopAuthMap.setTitleFlag(0);
                    shopAuthMap.setTitle("老板");
                } else {*/
                //默认职称flag为1,店家本人职称flag设置为0
                shopAuthMap.setTitleFlag(1);
                shopAuthMap.setTitle("员工");
                //}

                ShopAuthMapExecution sae = shopAuthMapService.addShopAuthMap(shopAuthMap);
                if (sae.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
                    return "shop/operationSuccess";
                } else {
                    return "shop/operationFail";
                }

            } catch (Exception e) {
                return "shop/operationFail";
            }
        }
        return "shop/operationFail";
    }

    /**
     * 检验二维码是否过期
     *
     * @param weChatInfo
     * @return
     */
    private boolean checkQRCodeInfo(WeChatInfo weChatInfo) {
        if (weChatInfo != null && weChatInfo.getShopId() != null && weChatInfo.getCreateTime() != null) {
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
     * 获取指定商店员工授权信息列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getShopAuthMapListByShopId")
    @ResponseBody
    private Map<String, Object> getShopAuthMapListByShopId(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //获取session中的商店对象
        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);

        //获取前端传来的访问页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);

        if (curShop != null && curShop.getShopId() > 0 && pageNum >= 0 && pageSize >= 0) {
            //默认从第1条开始,每页显示记录,进行分页
            PageHelper.startPage(pageNum, pageSize);
            ShopAuthMapExecution sae = shopAuthMapService.getShopAuthMapListByShopId(curShop.getShopId());
            PageInfo info = new PageInfo(sae.getShopAuthMapList());
            //返回列表给前端
            modelMap.put(SUCCESS, true);
            modelMap.put(SHOP_AUTH_MAP_LIST, sae.getShopAuthMapList());
            modelMap.put(COUNT, info.getPages());
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请传入正确信息查询");
        }
        return modelMap;
    }

    /**
     * 通过主键获取指定商店授权信息
     *
     * @param shopAuthId
     * @return
     */
    @GetMapping("/getShopAuthMapById")
    @ResponseBody
    private Map<String, Object> getShopAuthMapById(@RequestParam Integer shopAuthId) {

        Map<String, Object> modelMap = new HashMap<>();

        if (shopAuthId != null && shopAuthId > 0) {

            ShopAuthMap shopAuthMap = shopAuthMapService.getShopAuthMapById(shopAuthId);
            modelMap.put(SUCCESS, true);
            modelMap.put(SHOP_AUTH_MAP, shopAuthMap);

        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "商店授权ID不能为空");
        }
        return modelMap;
    }

    @PostMapping("/updateShopAuthMap")
    @ResponseBody
    private Map<String, Object> updateShopAuthMap(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //先判断是删除/回复授权(不需要验证码)编辑授权信息(需要验证码)
        boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
        //验证码校验
        if (!statusChange && !VerifyCodeUtil.checkVerifyCode(request)) {

            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误!");
            return modelMap;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ShopAuthMap shopAuthMap = null;
        //接收前端传来的json串
        String shopAuthMapStr = HttpServletRequestUtil.getString(request, SHOP_AUTH_MAP_STR);
        try {
            shopAuthMap = objectMapper.readValue(shopAuthMapStr, ShopAuthMap.class);
        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }
        if (shopAuthMap != null && shopAuthMap.getShopAuthId() != null) {
            try {
                if (!checkMaster(shopAuthMap.getShopAuthId())) {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, "无法对自身权限操作!");
                    return modelMap;
                }
                ShopAuthMapExecution sae = shopAuthMapService.updateShopAuthMap(shopAuthMap);

                if (sae.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);
                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, sae.getStateInfo());
                }

            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请选择正确授权信息");
        }
        return modelMap;
    }

    private boolean checkMaster(Integer shopAuthId) {
        //先查通过主键差授权信息,如果TitleFlag为0说明是店家不能移除自己权限
        ShopAuthMap shopAuthMap = shopAuthMapService.getShopAuthMapById(shopAuthId);
        if (shopAuthMap.getTitleFlag() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 删除员工
     *
     * @param shopAuthId
     * @return
     */
    @PostMapping("/removeShopAuthMap")
    @ResponseBody
    private Map<String, Object> removeShopAuthMap(@RequestParam Integer shopAuthId, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
        Integer shopId = curShop.getShopId();
        if (shopAuthId > 0) {
            try {
                ShopAuthMapExecution sae = shopAuthMapService.removeShopAuthMap(shopAuthId, shopId);
                if (sae.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);
                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, sae.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "该员工信息不存在");
        }
        return modelMap;
    }


}
