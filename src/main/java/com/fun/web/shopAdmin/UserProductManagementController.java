package com.fun.web.shopAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.dto.Echart.EchartSeries;
import com.fun.dto.Echart.EchartXAxis;
import com.fun.dto.shopAuthMap.ShopAuthMapExecution;
import com.fun.dto.userProductMap.UserProductMapExecution;
import com.fun.dto.wechat.UserAccessToken;
import com.fun.dto.wechat.WeChatInfo;
import com.fun.entity.*;
import com.fun.enums.ShopAuthMapStateEnum;
import com.fun.enums.UserProductMapStateEnum;
import com.fun.service.*;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.wechat.WeChatUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-17 20:27:34
 * @Desc: 用户消费商品记录
 */
@Controller
@RequestMapping("/shopAdmin")
public class UserProductManagementController {

    @Autowired
    private UserProductMapService userProductMapService;
    @Autowired
    private ProductSellDailyService productSellDailyService;
    @Autowired
    private WechatAuthService wechatAuthService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ShopAuthMapService shopAuthMapService;

    /**
     * 查询商品日销量列表(七天内)
     *
     * @param request
     * @return
     */
    @GetMapping("/getProductSellDailyList")
    @ResponseBody
    private Map<String, Object> getProductSellDailyList(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //获取session中的商店对象
        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);

        if (curShop != null && curShop.getShopId() > 0) {
            //添加查询条件
            ProductSellDaily productSellDailyCondition = new ProductSellDaily();
            productSellDailyCondition.setShop(curShop);
            //获取当前时间
            Calendar calendar = Calendar.getInstance();
            //获取昨天日期
            calendar.add(Calendar.DATE, -1);
            Date endTime = calendar.getTime();
            //获取七天前日期
            calendar.add(Calendar.DATE, -6);
            Date beginTime = calendar.getTime();
            //根据传入查询条件获取该商店销售情况
            List<ProductSellDaily> productSellDailyList = productSellDailyService.
                    getProductSellDailyList(productSellDailyCondition, beginTime, endTime);
            //指定日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //商品名列表,保证唯一
            HashSet<String> legendData = new HashSet<>();
            //X轴数据
            HashSet<String> xData = new HashSet<>();
            //定义series
            List<EchartSeries> series = new ArrayList<>();
            //接收商品日销售量列表
            List<Integer> totalList = new ArrayList<>();
            //当前商品名,默认为空
            String curProductName = "";
            for (int i = 0; i < productSellDailyList.size(); i++) {
                ProductSellDaily productSellDaily = productSellDailyList.get(i);
                //去重
                legendData.add(productSellDaily.getProduct().getProductName());
                xData.add(sdf.format(productSellDaily.getCreateTime()));
                if (!curProductName.equals(productSellDaily.getProduct().getProductName()) && !curProductName.isEmpty()) {
                    //如果当前商品名字不等于获取到的商品名字(或已遍历到列表末端),且当前商品名字不为空
                    //就是遍历到下一个商品的日销量信息,将当前一轮的信息放入series中(包过商品名及商品的统计日期和销量)
                    EchartSeries es = new EchartSeries();
                    es.setName(curProductName);
                    //克隆,不会影响现有数据
                    es.setData(totalList.subList(0, totalList.size()));
                    series.add(es);
                    //重置totalList
                    totalList = new ArrayList<>();
                    //变换curProductId为productId
                    curProductName = productSellDaily.getProduct().getProductName();
                    //继续添加新值
                    totalList.add(productSellDaily.getTotal());
                } else {
                    //如果还是当前的productId继续添加
                    totalList.add(productSellDaily.getTotal());
                    curProductName = productSellDaily.getProduct().getProductName();
                }
                //队列末尾,要将最后一个商品信息也填上
                if (i == productSellDailyList.size() - 1) {
                    EchartSeries es = new EchartSeries();
                    es.setName(curProductName);
                    es.setData(totalList.subList(0, totalList.size()));
                    series.add(es);
                }
            }
            modelMap.put("series", series);
            modelMap.put("legendData", legendData);
            //拼接处xAxis
            List<EchartXAxis> xAxis = new ArrayList<>();
            EchartXAxis exa = new EchartXAxis();
            exa.setData(xData);
            xAxis.add(exa);
            modelMap.put("xAxis", xAxis);
            modelMap.put(SUCCESS, true);
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入正确条件查询");
        }
        return modelMap;
    }

    /**
     * 获取用户在某商店的消费记录
     *
     * @param request
     * @return
     */
    @GetMapping("/getUserProductMapByShop")
    @ResponseBody
    private Map<String, Object> getUserProductMapByShop(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //获取session中的商店对象
        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);

        //获取前端传来的访问页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);

        if (curShop != null && curShop.getShopId() > 0 && pageNum >= 0 && pageSize >= 0) {
            //添加查询条件
            UserProductMap userProductMapCondition = new UserProductMap();
            userProductMapCondition.setShop(curShop);
            String productName = HttpServletRequestUtil.getString(request, PRODUCT_NAME);
            if (productName != null && !productName.equals("")) {
                Product product = new Product();
                product.setProductName(productName);
                userProductMapCondition.setProduct(product);
            }
            PageHelper.startPage(pageNum, pageSize);
            List<UserProductMap> userProductMapList = userProductMapService.getProductMap(userProductMapCondition);
            PageInfo info = new PageInfo(userProductMapList);
            modelMap.put(SUCCESS, true);
            modelMap.put(USER_PRODUCT_MAP_LIST, userProductMapList);
            modelMap.put(COUNT, info.getPages());
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入正确条件查询");
        }
        return modelMap;
    }

    /**
     * 添加用户消费记录
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping("/addUserProductMap")
    private String addUserProductMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            //添加消费记录所需参数
            Integer productId = weChatInfo.getProductId();
            Integer customerId = weChatInfo.getCustomerId();

            UserProductMap userProductMap = compactUserProductMap(customerId, productId);
            //获取微信实体类扫码传来的shopId
            userProductMap.setShopId(weChatInfo.getShopId());
            if (userProductMap != null && customerId > 0) {
                try {
                    //校验当前操作的人是否是本店员工且有权限
                    if (!checkShopAuth(operator.getUserId(), userProductMap)) {
                        return "shop/operationFail";
                    }
                    //添加消费记录
                    UserProductMapExecution upe = userProductMapService.addUserProductMap(userProductMap);
                    if (upe.getState() == UserProductMapStateEnum.SUCCESS.getState()) {
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
     * 校验操作员是否有权限
     *
     * @param userId
     * @param userProductMap
     * @return
     */
    private boolean checkShopAuth(Integer userId, UserProductMap userProductMap) {
        //查询该商店下授权信息
        ShopAuthMapExecution sae = shopAuthMapService.getShopAuthMapListByShopId(userProductMap.getShopId());
        //遍历授权信息,匹配当前操作员的id和当前商店授权信息的员工id一致并且状态可用返回true
        for (ShopAuthMap shopAuthMap : sae.getShopAuthMapList()) {
            if (shopAuthMap.getEmployee().getUserId() == userId && shopAuthMap.getEnableStatus() != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 封装用户消费记录所需参数
     *
     * @param customerId
     * @param productId
     * @return
     */
    private UserProductMap compactUserProductMap(Integer customerId, Integer productId) {
        UserProductMap userProductMap = new UserProductMap();
        if (customerId > 0 && productId > 0) {
            PersonInfo user = new PersonInfo();
            user.setUserId(customerId);
            //获取商品积分
            Product product = productService.getProductById(productId);
            userProductMap.setUser(user);
            userProductMap.setShop(product.getShop());
            userProductMap.setPoint(product.getPoint());
            userProductMap.setProduct(product);
            userProductMap.setCreateTime(new Date());
            //获取当前商品所属商店

            return userProductMap;
        } else {
            return null;
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
     * 检验二维码是否过期
     *
     * @param weChatInfo
     * @return
     */
    private boolean checkQRCodeInfo(WeChatInfo weChatInfo) {
        if (weChatInfo != null && weChatInfo.getProductId() != null
                && weChatInfo.getCustomerId() != null && weChatInfo.getCreateTime() != null&&weChatInfo.getShopId()!=null) {
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
