package com.fun.web.shopAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.dto.shop.ShopExecution;
import com.fun.entity.Area;
import com.fun.entity.PersonInfo;
import com.fun.entity.Shop;
import com.fun.entity.ShopCategory;
import com.fun.enums.ShopStateEnum;
import com.fun.exceptions.ShopOperationException;
import com.fun.service.AreaService;
import com.fun.service.ShopCategoryService;
import com.fun.service.ShopService;
import com.fun.util.Image.ImageHelper;
import com.fun.util.Image.ImageUtil;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.verifycode.VerifyCodeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-02 12:53:39
 * @Desc: 商店管理
 */


@Controller
@RequestMapping("/shopAdmin")
public class ShopManagementController {

    //商店
    @Autowired
    private ShopService shopService;
    //商店类别
    @Autowired
    private ShopCategoryService shopCategoryService;
    //区域类别
    @Autowired
    private AreaService areaService;

    /**
     * 验证商店管理
     * 未经过登录或者店铺列表过来的进行重定向
     *
     * @param request
     * @return
     */
    @GetMapping("/getShopManagementInfo")///{shopId} @PathVariable Integer shopId
    @ResponseBody
    private Map<String, Object> getShopManagementInfo(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        //获得请求域中的shopId
        Integer shopId = HttpServletRequestUtil.getInt(request, SHOP_ID);

        //如果shopId<=0(未登录)
        if (shopId <= 0) {

            //获取session中的当前shop对象
            Object curShopObject = request.getSession().getAttribute(CURRENT_SHOP);

            //如果当shop对象为空(违规操作),进行重定向,返回到店铺列表
            if (curShopObject == null) {
                modelMap.put(REDIRECT, true);
                modelMap.put(URL, "/O2O/shopAdmin/shopList");

            } else {//否则不进行重定向并且把当前shop对象的shopId返回
                Shop curShop = (Shop) curShopObject;
                modelMap.put(REDIRECT, false);
                modelMap.put(SHOP_ID, curShop.getShopId());
            }

        } else {//如果shopId存在(登陆了),设置一个shop对象传入请求域中的shopId返回到session中的当前shop对象中
            Shop curShop = new Shop();
            curShop.setShopId(shopId);
            modelMap.put(SHOP_ID, shopId);
            request.getSession().setAttribute(CURRENT_SHOP, curShop);
            modelMap.put(REDIRECT, false);
        }
        return modelMap;
    }

    /**
     * 获取商店列表分页-商店管理(高级查询(多条件),分页,模糊查询)
     *
     * @param request
     * @return
     */
    @GetMapping("/getShopListPageHelper")
    @ResponseBody
    private Map<String, Object> getShopListPageHelper(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        PersonInfo user = new PersonInfo();
        //TODO 添加用户
        //user.setUserId(1);
        //user.setName("小凡");
        //request.getSession().setAttribute(USER, user);

        //获取前端传来的访问页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);

        //获取用户信息
        user = (PersonInfo) request.getSession().getAttribute(USER);

        if (user != null && user.getUserId() > 0 && pageNum >= 0 && pageSize >= 0) {
            try {
                Shop shopCondition = new Shop();
                shopCondition.setOwner(user);
                //根据商品名字查询
                String shopName = HttpServletRequestUtil.getString(request, SHOP_NAME);
                if (shopName != null && !shopName.equals("")) {
                    shopCondition.setShopName(shopName);
                }

                //默认从第1条开始,每页6条记录,进行分页
                PageHelper.startPage(pageNum, pageSize);
                ShopExecution se = shopService.selectShopListByPageHelper(shopCondition);
                //PageInfo<Shop> info = new PageInfo<>(se.getShopList(), pageSize);
                PageInfo info = new PageInfo(se.getShopList());

                //返回商店列表给前台包括分页的页数
                modelMap.put(SHOP_LIST, se.getShopList());
                //将商店列表存入session,作为用户权限验证一句,该用户只能操作自己的商店
                request.getSession().setAttribute(SHOP_LIST, se.getShopList());
                modelMap.put(USER, user);
                modelMap.put(SUCCESS, true);
                modelMap.put(COUNT, info.getPages());

            } catch (ShopOperationException e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.getMessage());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请传入正确信息查询");
        }
        return modelMap;
    }


    /**
     * 获取商店列表-商店管理(高级查询(多条件),分页,模糊查询)
     *
     * @param 暂时弃用
     * @return
     */
    /*
    @GetMapping("/getShopList")
    @ResponseBody
    private Map<String, Object> getShopList(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        PersonInfo user = new PersonInfo();
        //TODO SESSION添加用户
        user.setUserId(1);
        user.setName("小凡");
        request.getSession().setAttribute(USER, user);
        int pageIndex = HttpServletRequestUtil.getInt(request, PAGE_INDEX);//Integer.parseInt(request.getParameter("pageIndex"));
        //获取用户信息
        user = (PersonInfo) request.getSession().getAttribute(USER);

        if (user != null) {

            try {
                Shop shopCondition = new Shop();
                shopCondition.setOwner(user);

                //默认从第1条开始,每页6条记录
                ShopExecution se = shopService.selectShopList(shopCondition, pageIndex, 6);

                modelMap.put(SHOP_LIST, se.getShopList());
                modelMap.put(USER, user);
                modelMap.put(SUCCESS, true);
                modelMap.put("count", se.getCount());

            } catch (ShopOperationException e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.getMessage());
                return modelMap;
            }

        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "用户不存在无法操作!");
        }
        return modelMap;
    }
*/

    /**
     * 获取商店信息
     *
     * @param request
     * @return
     */
    @GetMapping("/getShopById")
    @ResponseBody
    private Map<String, Object> getShopById(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        Integer shopId = HttpServletRequestUtil.getInt(request, SHOP_ID);

        if (shopId > 0) {
            try {
                Shop shop = shopService.getShopById(shopId);
                List<Area> areaList = areaService.getAreaList();

                //如果查到了shop返回
                if (shop != null && areaList != null) {
                    modelMap.put(SHOP, shop);
                    modelMap.put(AREA_LIST, areaList);
                    modelMap.put(SUCCESS, true);


                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, "要访问数据跑到火星去了^_^");
                }

            } catch (ShopOperationException e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.getMessage());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "商店ID不存在");
        }
        return modelMap;
    }

    /**
     * 修改商店信息
     *
     * @param request
     * @return
     */
    @PostMapping("/updateShop")
    @ResponseBody
    private Map<String, Object> updateShop(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //进行验证码的验证
        if (!VerifyCodeUtil.checkVerifyCode(request)) {

            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误!");
            return modelMap;
        }
        //1.接收并转换相应参数,包括店铺信息及图片信息
        String shopStr = HttpServletRequestUtil.getString(request, SHOP_STR);

        ObjectMapper objectMapper = new ObjectMapper();

        Shop shop = null;

        try {//将前端接收到的参数转为实体类
            shop = objectMapper.readValue(shopStr, Shop.class);

        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.getMessage());
            return modelMap;
        }
        //上传的图片
        CommonsMultipartFile shopImg = null;

        //获取前端页面的上下文信息存储为CommonsMultipart
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());

        //如果前端传的文件具备Multipart文件流进行强制转换,接收到shopImg中
        if (commonsMultipartResolver.isMultipart(request)) {

            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

            shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");

            try {
                //一定先进行非空判断在调用判断文件流方法,否则空指针异常
                if (shopImg != null) {
                    if (!ImageUtil.isImg(shopImg.getInputStream())) {
                        modelMap.put(SUCCESS, false);

                        modelMap.put(ERR_MSG, "只能上传图片!");

                        return modelMap;
                    }
                }

            } catch (IOException e) {
                modelMap.put(SUCCESS, false);

                modelMap.put(ERR_MSG, "只能上传图片!" + e.toString());

                return modelMap;
            }
        }
        //2.修改店铺
        if (shop != null && shop.getShopId() != null) {

            //如果shop和shopImg都不为空,添加店铺,传入通过前端参数生成的实体类shop和shopImg
            ShopExecution se = null;

            try {
                //图片不为空才能获取InputStream否则空指针异常
                if (shopImg != null) {
                    //传入名字和流进行合并封装
                    ImageHelper imageHelper = new ImageHelper(shopImg.getOriginalFilename(), shopImg.getInputStream());
                    se = shopService.updateShop(shop, imageHelper);
                }
                //如果不修改图片,给一个空的ImageHelper,Service层在进行判断
                else {
                    se = shopService.updateShop(shop, null);
                }
                //如果添加后返回的对象的结果状态和Shop枚举中的SUCCESS状态一致修改成功
                if (se.getState() == ShopStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);

                } else {
                    //否则修改失败,返回失败的信息
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, se.getStateInfo());
                }

            } catch (ShopOperationException e) {
                //否则修改失败,返回失败的信息
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;

            } catch (IOException e) {
                //否则修改失败,返回失败的信息
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }

        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入店铺Id");
        }
        //3.返回结果
        return modelMap;

    }

    /**
     * 商店注册
     *
     * @param request
     * @return
     */
    @PostMapping("/registerShop")
    @ResponseBody
    private Map<String, Object> registerShop(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        //进行验证码的验证
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误!");
            return modelMap;
        }

        //1.接收并转换相应参数,包括店铺信息及图片信息
        String shopStr = HttpServletRequestUtil.getString(request, SHOP_STR);

        ObjectMapper objectMapper = new ObjectMapper();

        Shop shop = null;

        try {//将前端接收到的参数转为实体类
            shop = objectMapper.readValue(shopStr, Shop.class);

        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }

        //上传的图片
        CommonsMultipartFile shopImg = null;

        //获取前端页面的上下文信息存储为CommonsMultipart
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());

        //如果前端传的文件具备Multipart文件流进行强制转换,接收到shopImg中
        if (commonsMultipartResolver.isMultipart(request)) {

            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

            shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");

            try {
                if (!ImageUtil.isImg(shopImg.getInputStream())) {

                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, "只能上传图片!");
                    return modelMap;
                }
            } catch (IOException e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, "只能上传图片!" + e.toString());
                return modelMap;
            }

        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "上传图片不能为空");
            return modelMap;
        }
        //2.注册店铺
        if (shop != null && shopImg != null) {
            //session中获取用户
            PersonInfo owner = (PersonInfo) request.getSession().getAttribute(USER);

            //如果用户不存在为空
            if (owner == null) {
                //否则注册失败,返回失败的信息
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, "未登录请先登录");
                return modelMap;
            }

            //店铺和用户绑定
            shop.setOwner(owner);

            //如果shop和shopImg都不为空,添加店铺,传入通过前端参数生成的实体类shop和shopImg
            ShopExecution se = null;

            try {
                //传入名字和流进行合并封装
                ImageHelper imageHelper = new ImageHelper(shopImg.getOriginalFilename(), shopImg.getInputStream());

                se = shopService.addShop(shop, imageHelper);

                //如果添加后返回的对象的结果状态和Shop枚举中的CHECK状态一致注册成功
                if (se.getState() == ShopStateEnum.CHECK.getState()) {

                    modelMap.put(SUCCESS, true);

                    //只有该用户创建的店铺才能操作,获取该用户可以操作的店铺列表
                    //@SuppressWarnings("unchecked")
                    List<Shop> shopList = (List<Shop>) request.getSession().getAttribute(SHOP_LIST);

                    //如果可以操作的商店列表为空(第一次创建店铺),把店铺信息存回到session
                    if (shopList == null || shopList.size() == 0) {
                        shopList = new ArrayList<>();
                    }
                    //如果不是一次创建店铺,也把创建的店铺存到session
                    shopList.add(se.getShop());
                    request.getSession().setAttribute(SHOP_LIST, shopList);

                } else {
                    //否则注册失败,返回失败的信息
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, se.getStateInfo());
                }

            } catch (ShopOperationException e) {
                //否则注册失败,返回失败的信息
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;

            } catch (IOException e) {
                //否则注册失败,返回失败的信息
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入店铺信息");
        }
        return modelMap;

    }

    /**
     * 获取商店初始信息(店铺类别和区域类别)
     *
     * @return
     */
    @GetMapping("/getShopInitInfo")
    @ResponseBody
    private Map<String, Object> getShopInitInfo() {

        Map<String, Object> modelMap = new HashMap<>();

        List<Area> areaList = new ArrayList<>();

        List<ShopCategory> shopCategoryList = new ArrayList<>();

        try {
            areaList = areaService.getAreaList();
            shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());

            modelMap.put(AREA_LIST, areaList);
            modelMap.put(SHOP_CATEGORY_LIST, shopCategoryList);
            modelMap.put(SUCCESS, true);

        } catch (ShopOperationException e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }
        return modelMap;
    }

/*
    private static void inputStreamToFile(InputStream inputStream, File file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new RuntimeException("调用InputStreamToFile产生异常" + e.toString());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("InputStreamToFile关闭IO产生异常" + e.toString());
            }
        }
    }
*/
}
