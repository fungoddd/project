package com.fun.web.shopAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.dto.product.ProductExecution;
import com.fun.entity.Product;
import com.fun.entity.ProductCategory;
import com.fun.entity.Shop;
import com.fun.enums.ProductStateEnum;
import com.fun.exceptions.ProductOperationException;
import com.fun.service.ProductCategoryService;
import com.fun.service.ProductService;
import com.fun.util.Image.ImageHelper;
import com.fun.util.Image.ImageUtil;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.verifycode.VerifyCodeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
 * @Date: 2018-12-02 01:29:32
 * @Desc: 商品管理
 */

@Controller
@RequestMapping("/shopAdmin")
public class ProductManagementController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    //上传详情图片最大数量,5张
    private static final int MAX_IMAGE_COUNT = 5;

    /**
     * 删除指定商店下的商品
     *
     * @param request
     * @return
     */
    @PostMapping("/deleteProduct")
    @ResponseBody
    private Map<String, Object> deleteProduct(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //获取session中的shop对象
        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
        Integer productId = HttpServletRequestUtil.getInt(request, PRODUCT_ID);

        if (curShop != null && curShop.getShopId() >= 0 && productId >= 0) {
            try {
                ProductExecution pe = productService.deleteProductByShopIdAndPid(curShop.getShopId(), productId);
                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请登陆后指定商品在操作");
        }
        return modelMap;
    }

    /**
     * 通过商店获得该商店下商品列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getProductListByShop")
    @ResponseBody
    private Map<String, Object> getProductListByShop(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //获取前端传来的访问页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);
        //session中获取当前商店
        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);

        if (curShop != null && curShop.getShopId() >= 0 && pageNum >= 0 && pageSize >= 0) {
            try {
                //查询条件通过商品类别或者商品名字
                Integer productCategoryId = HttpServletRequestUtil.getInt(request, PRODUCT_CATEGORY_ID);
                String productName = HttpServletRequestUtil.getString(request, PRODUCT_NAME);

                //进行查询条件的筛选
                Product product = compactProductCondition(curShop, productCategoryId, productName);

                //进行分页,传入筛选过查询条件的商品对象进行查询
                PageHelper.startPage(pageNum, pageSize);
                ProductExecution pe = productService.getProductListByPageHelper(product);
                //PageInfo<Product> info = new PageInfo<>(pe.getProductList(), pageSize);
                PageInfo info = new PageInfo(pe.getProductList());
                //返回商店列表给前台包括分页的页数
                modelMap.put(PRODUCT_LIST, pe.getProductList());
                modelMap.put(COUNT, info.getPages());
                modelMap.put(SUCCESS, true);

            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.getMessage());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请指定商店在进行查询");
        }
        return modelMap;
    }

    /**
     * 商品修改
     *
     * @param request
     * @return
     */
    @PostMapping("/updateProduct")
    @ResponseBody
    private Map<String, Object> updateProduct(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        //如果是修改操作进行验证码验证,上下架操作不验证
        boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");

        //验证码校验
        if (!statusChange && !VerifyCodeUtil.checkVerifyCode(request)) {

            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误!");
            return modelMap;
        }
        //接收到前端的参数,商品,缩略图,详情图转为对应的实体类
        ObjectMapper objectMapper = new ObjectMapper();

        Product product = null;

        String productStr = HttpServletRequestUtil.getString(request, PRODUCT_STR);

        try {
            product = objectMapper.readValue(productStr, Product.class);

        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }
        //上传的缩略图
        ImageHelper thumbnail = null;
        //上传的详情图列表
        List<ImageHelper> productImgList = new ArrayList<>();

        //获取请求域中的文件流如果包含文件流,取出文件(缩略图详情图列表)存储为CommonsMultipart
        CommonsMultipartResolver commonsMultipartResolver =
                new CommonsMultipartResolver(request.getSession().getServletContext());
        //如果请求中存在图片文件流,取出相关图片进行操作
        try {
            //如果前端传的文件具备Multipart文件流进行强制转换,接收到imageHelper和productImgList中
            if (commonsMultipartResolver.isMultipart(request)) {

                MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                //调用封装好的函数getImageHolder传入得到的文件流,传入自身ImageHelper(缩略图对象)和详情图列表
                //进行处理,返回得到处理后的缩略图,同时也给productImgList进行了处理并赋值
                thumbnail = getImageHolder(multipartHttpServletRequest, thumbnail, productImgList);
            }
        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }
        if (product != null) {
            try {
                //从session中获取当前商店id赋给product,减少对前端传来的数据依赖
                Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
                product.setShop(curShop);
                //更新操作
                ProductExecution pe = productService.updateProduct(product, thumbnail, productImgList);
                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);
                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, pe.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入商品信息!");
        }
        return modelMap;

    }

    /**
     * 获取商品信息通过商品id
     *
     * @param productId
     * @return
     */
    @GetMapping("/getProductById")
    @ResponseBody
    private Map<String, Object> getProductById(@RequestParam Integer productId, HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        if (productId >= 0) {
            //获取到指定商品
            Product product = productService.getProductById(productId);
            String name = product.getProductDesc();
            //获取到该商店下商品类别
            List<ProductCategory> productCategoryList =
                    productCategoryService.getProductCategoryList(product.getShop().getShopId());
            if (product != null) {
                modelMap.put(PRODUCT, product);
                modelMap.put(PRODUCT_CATEGORY_LIST, productCategoryList);
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

    @PostMapping("/addProduct")
    @ResponseBody
    private Map<String, Object> addProduct(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        //校验验证码
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误!");
            return modelMap;
        }

        //接收到前端的参数,商品,缩略图,详情图转为对应的实体类
        ObjectMapper objectMapper = new ObjectMapper();

        Product product = null;

        String productStr = HttpServletRequestUtil.getString(request, PRODUCT_STR);

        try {
            product = objectMapper.readValue(productStr, Product.class);

        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }
        //上传的缩略图
        ImageHelper thumbnail = null;
        //上传的详情图列表
        List<ImageHelper> productImgList = new ArrayList<>();

        //获取请求域中的文件流如果包含文件流,取出文件(缩略图详情图列表)存储为CommonsMultipart
        CommonsMultipartResolver commonsMultipartResolver =
                new CommonsMultipartResolver(request.getSession().getServletContext());

        try {
            //如果前端传的文件具备Multipart文件流进行强制转换,接收到imageHelper和productImgList中
            if (commonsMultipartResolver.isMultipart(request)) {

                MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                //调用封装好的函数getImageHolder传入得到的文件流,传入自身ImageHelper(缩略图对象)和详情图列表
                //进行处理,返回得到处理后的缩略图,同时也给productImgList进行了处理并赋值
                thumbnail = getImageHolder(multipartHttpServletRequest, thumbnail, productImgList);

            } else {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, "上传图片不能为空");
                return modelMap;
            }

        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "只能上传图片!");
            return modelMap;
        }
        //如果缩略图详情图没有问题进行商品添加
        if (product != null && thumbnail != null && productImgList.size() > 0) {
            try {
                //获取session中的店铺id给product,尽量减少对前端传来的数据依赖
                Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);
                product.setShop(curShop);
                //添加商品
                ProductExecution pe = productService.addProduct(product, thumbnail, productImgList);

                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);

                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, pe.getStateInfo());

                }
            } catch (ProductOperationException e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "添加失败");

        }
        return modelMap;

    }
///////////////////////////////////////////

    /**
     * 获取当前查询条件:查询指定商店下的商品列表,商品类别id查询商品,商品名字查询
     *
     * @param shop
     * @param productCategoryId
     * @param productName
     * @return
     */
    private Product compactProductCondition(Shop shop, Integer productCategoryId, String productName) {

        Product productCondition = new Product();
        //把session中的当前shop对象添加到商品对象中,指定查询该商店下的商品
        productCondition.setShop(shop);

        // 若有指定类别的要求则添加进去
        if (productCategoryId >= 0) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }
        // 若有商品模糊查询的要求则添加进去
        if (productName != null && !productName.equals("")) {
            productCondition.setProductName(productName);
        }
        return productCondition;
    }

    /**
     * 缩略图和详情图列表处理
     *
     * @param request
     * @param thumbnail
     * @param productImgList
     * @return
     * @throws IOException
     */
    private ImageHelper getImageHolder(MultipartHttpServletRequest request, ImageHelper thumbnail, List<ImageHelper> productImgList) throws IOException {

        MultipartHttpServletRequest multipartHttpServletRequest = request;
        // 取出缩略图并构建imageHelper对象
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("thumbnail");

        if (thumbnailFile != null) {
            //验证是否为图片
            if (!ImageUtil.isImg(thumbnailFile.getInputStream())) {

                throw new ProductOperationException("只能上传图片!");
            }

            thumbnail = new ImageHelper(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        }

        //取出详情图列表构建productImgList,5张
        for (int i = 0; i < MAX_IMAGE_COUNT; i++) {

            CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("productImg" + i);


            if (productImgFile != null) {
                // 若取出的第i个详情图片文件流不为空，则将其加入详情图列表
                ImageHelper productImg = new ImageHelper(productImgFile.getOriginalFilename(), productImgFile.getInputStream());
                productImgList.add(productImg);

                //验证是否为图片
                if (!ImageUtil.isImg(productImgFile.getInputStream())) {
                    throw new ProductOperationException("只能上传图片!");
                }

            } else {
                // 若取出的第i个详情图片文件流为空，则终止循环
                break;
            }
        }
        //返回处理后的缩略图
        return thumbnail;
    }
}
