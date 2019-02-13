package com.fun.web.wechat;

import com.fun.dto.wechat.UserAccessToken;
import com.fun.dto.wechat.WeChatUser;
import com.fun.dto.wechat.WechatAuthExecution;
import com.fun.entity.PersonInfo;
import com.fun.entity.UserAwardMap;
import com.fun.entity.WechatAuth;
import com.fun.enums.WechatAuthStateEnum;
import com.fun.service.PersonInfoService;
import com.fun.service.WechatAuthService;
import com.fun.util.baidu.ShortNetAddress;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.verifycode.VerifyCodeUtil;
import com.fun.util.wechat.WeChatUtil;
import com.fun.web.front.MyAwardController;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

import static com.fun.constant.ControllerConst.*;

@Controller
@RequestMapping("wechatLogin")
/**

 * @Author: FunGod
 * @Date: 2018-12-07 11:19:03
 * @Desc: 获取关注公众号之后的微信用户信息的接口，如果在微信浏览器里访问
 * https://open.weixin.qq.com/connect/oauth2/authorize?appid=您的appId&redirect_uri=http://www.woaiguoziqi.我爱你/o2o/wechatlogin/logincheck&role_type=1&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect
 * 则这里将会获取到code,之后再可以通过code获取到access_token 进而获取到用户信息
 *
 */

public class WeChatLoginController {

    private static Logger logger = LoggerFactory.getLogger(WeChatLoginController.class);
    //通过这个状态判断是点店家入口进来的还是普通用户
    private static final String FRONTGOTO = "1";
    private static final String SHOPGOTO = "2";

    @Autowired
    private WechatAuthService wechatAuthService;
    @Autowired
    private PersonInfoService personInfoService;
    //微信获取用户信息的api前缀
    private static String urlPrefix;

    //微信获取用户信息的api中间部分
    private static String urlMiddle;

    //微信获取用户信息的api后缀
    private static String urlSuffix;

    //微信回传的响应的url
    private static String loginUrl;

    @Value("${wechat.prefix}")
    public void setUrlPrefix(String urlPrefix) {
        WeChatLoginController.urlPrefix = urlPrefix;
    }


    @Value("${wechat.middle}")
    public void setUrlMiddle(String urlMiddle) {
        WeChatLoginController.urlMiddle = urlMiddle;
    }

    @Value("${wechat.suffix}")
    public void setUrlSuffix(String urlSuffix) {
        WeChatLoginController.urlSuffix = urlSuffix;
    }

    @Value("${wechat.login.url}")
    public void setLoginUrl(String loginUrl) {
        WeChatLoginController.loginUrl = loginUrl;
    }

    /**
     * 微信的登录注册
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/loginCheck")
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("wecat login get...");
        // 获取微信公众号传输过来的code,通过code可获取access_token,进而获取用户信息
        String code = request.getParameter(CODE);
        // 这个state用来传自定义的信息,方便程序调用
        String roleType = request.getParameter("state");
        logger.debug("wecat login code:" + code);

        //本服务号上的微信实体类
        WechatAuth auth = null;
        //微信用户实体类
        WeChatUser user = null;
        //微信号的openId
        String openId = null;

        if (code != null) {
            UserAccessToken token;
            try {
                //通过code获取access_token
                token = WeChatUtil.getUserAccessToken(code);
                logger.debug("wechat login token:" + token.toString());
                // 通过token获取accessToken
                String accessToken = token.getAccessToken();
                // 通过token获取openId
                openId = token.getOpenId();
                // 通过access_token和openId获取用户昵称等信息
                user = WeChatUtil.getUserInfo(accessToken, openId);

                logger.debug("wechat login user:" + user.toString());
                //设置openId到session中
                request.getSession().setAttribute(OPEN_ID, openId);
                //在数据库中通过token得到的openId查询微信用户信息
                auth = wechatAuthService.getWechatUserByOpenId(openId);
            } catch (IOException e) {
                logger.error("error in getUserAccessToken or getUserInfo or findByOpenId: " + e.toString());
                e.printStackTrace();
            }
        }
        logger.debug("wechat login success");
        logger.debug("login usertype:" + roleType);

        //如果在数据库中没有此微信号信息
        if (auth == null) {
            //就把微信号的用户信息设置到自己的personInfo中,并创建本站的微信号信息添加进去,进行注册
            PersonInfo personInfo = WeChatUtil.getPersonInfoRequest(user);
            //如果店铺页注册的,就设置为普通用户
            if (FRONTGOTO.equals(roleType)) {
                personInfo.setUserType(1);
            }
            //如果店家页进来的就设置为店家用户
            if (SHOPGOTO.equals(roleType)) {
                personInfo.setUserType(2);
            }
            auth = new WechatAuth();
            auth.setPersonInfo(personInfo);
            auth.setOpenId(openId);
            //进行注册
            WechatAuthExecution we = wechatAuthService.register(auth);
            //如果注册失败什么也做不了
            if (we.getState() != WechatAuthStateEnum.SUCCESS.getState()) {
                return null;
            } else {
                //注册成功获取用户信息(通过刚绑定在本站的微信号中的用户中的userId)
                personInfo = personInfoService.getPersonInfoById(auth.getPersonInfo().getUserId());
                //设置到session中
                request.getSession().setAttribute(USER, personInfo);
            }

        } else {
            PersonInfo personInfo = personInfoService.getPersonInfoById(auth.getPersonInfo().getUserId());
            request.getSession().setAttribute(USER, personInfo);
        }
        //如果是店铺页进来的,跳转到前端展示(公众号按钮)
        if (FRONTGOTO.equals(roleType)) {
            return "front/index";
        } else {//店家跳转到店家管理,return的是视图,静态路径
            return "shop/shoplist";
        }
    }


    /**
     * 生成带有URL的二维码,实现微信扫码跳转(商家登录)
     */
    @GetMapping("/generateQRCode4ShopAdminLogin")
    @ResponseBody
    private void generateQRCode4ShopAdminLogin(HttpServletResponse response) {
        try {
            //将content信息进行base64编码避免特殊字符干扰,之后拼接成目标URL
            String longUrl = urlPrefix + loginUrl + urlMiddle + "2" + urlSuffix;
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

    /**
     * 生成带有URL的二维码,实现微信扫码跳转(普通用户登录)
     */
    @GetMapping("/generateQRCode4UserLogin")
    @ResponseBody
    private void generateQRCode4UserLogin(HttpServletResponse response) {
        try {
            String longUrl = urlPrefix + loginUrl + urlMiddle + "1" + urlSuffix;
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

