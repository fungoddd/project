package com.fun.web.front;

import com.fun.entity.PersonInfo;
import com.fun.entity.Product;
import com.fun.entity.Shop;
import com.fun.service.ProductService;
import com.fun.service.WechatAuthService;
import com.fun.util.baidu.ShortNetAddress;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.verifycode.VerifyCodeUtil;
import com.fun.web.shopAdmin.ShopAuthManagementController;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-10 22:05:31
 * @Desc: 商品详情
 */
@Controller
@RequestMapping("/front")
public class ProductDetailController {
    @Autowired
    private ProductService productService;
    @Autowired
    private WechatAuthService wechatAuthService;

    //微信获取用户信息的api前缀
    private static String urlPrefix;

    //微信获取用户信息的api中间部分
    private static String urlMiddle;

    //微信获取用户信息的api后缀
    private static String urlSuffix;

    //微信回传的响应的url
    private static String productMapUrl;

    @Value("${wechat.prefix}")
    public void setUrlPrefix(String urlPrefix) {
        ProductDetailController.urlPrefix = urlPrefix;
    }


    @Value("${wechat.middle}")
    public void setUrlMiddle(String urlMiddle) {
        ProductDetailController.urlMiddle = urlMiddle;
    }

    @Value("${wechat.suffix}")
    public void setUrlSuffix(String urlSuffix) {
        ProductDetailController.urlSuffix = urlSuffix;
    }

    @Value("${wechat.addUserproductMap.url}")
    public void setAddProductMapUrl(String productMapUrl) {
        ProductDetailController.productMapUrl = productMapUrl;
    }

    /**
     * 生成带有URL的二维码,实现微信扫码跳转
     */
    @GetMapping("/generateQRCode4product")
    @ResponseBody
    private void generateQRCode4product(HttpServletRequest request, HttpServletResponse response) {
        //获取前端传来的商品Id
        Integer productId = HttpServletRequestUtil.getInt(request, PRODUCT_ID);
        //获取shopId
        Integer shopId = HttpServletRequestUtil.getInt(request, SHOP_ID);
        //获取session中用户信息
        PersonInfo user = (PersonInfo) request.getSession().getAttribute(USER);
        if (productId > 0 && user != null && user.getUserId() != null) {
            //获取当前时间戳,保证二维码时间有效性,精确到毫秒
            long timeStamp = System.currentTimeMillis();
            //将商店id和时间戳传入content,赋值到state中,微信获取到这些信息会回传到授权信息添加授权
            String content = "{aaaproductIdaaa:" + productId + ",aaashopIdaaa:" + shopId + ",aaacustomerIdaaa:" + user.getUserId() + ",aaacreateTimeaaa:" + timeStamp + "}";
            try {
                //将content信息进行base64编码避免特殊字符干扰,之后拼接成目标URL
                String longUrl = urlPrefix + productMapUrl + urlMiddle + URLEncoder.encode(content, "UTF-8")
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
     * 获取商品信息通过商品id
     *
     * @param request
     * @return
     */
    @GetMapping("/getProductDetail")
    @ResponseBody
    private Map<String, Object> getProductById(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        Integer productId = HttpServletRequestUtil.getInt(request, PRODUCT_ID);
        if (productId >= 0) {
            //获取到指定商品
            Product product = productService.getProductById(productId);
            //获取session中用户信息,不为空显示验证码
            PersonInfo user = (PersonInfo) request.getSession().getAttribute(USER);
            if (user == null) {
                modelMap.put("needQRCode", false);
            } else {
                modelMap.put("needQRCode", true);
            }
            if (product != null) {
                modelMap.put(PRODUCT, product);
                modelMap.put(SUCCESS, true);
            } else {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, "该商品跑到火星去了^_^");
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "商品id不存在");
        }
        return modelMap;
    }
}
