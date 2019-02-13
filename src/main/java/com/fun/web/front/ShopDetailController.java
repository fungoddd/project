package com.fun.web.front;

import com.fun.dto.product.ProductExecution;
import com.fun.entity.Product;
import com.fun.entity.ProductCategory;
import com.fun.entity.Shop;
import com.fun.service.ProductCategoryService;
import com.fun.service.ProductService;
import com.fun.service.ShopService;
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
 * @Date: 2018-12-05 16:53:05
 * @Desc: 商店详情信息
 */
@Controller
@RequestMapping("/front")
public class ShopDetailController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductService productService;

    /**
     * 获取指定商店下的所有商品
     *
     * @return
     */
    @GetMapping("/getProductByShop")
    @ResponseBody
    private Map<String, Object> getProductBySHop(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //获取前端传来的请求:页码,每页显示记录数,shopId
        Integer pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        Integer pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);
        Integer shopId = HttpServletRequestUtil.getInt(request, SHOP_ID);

        if (pageNum >= 0 && pageSize >= 0 && shopId > 0) {
            //获取前端传来的productCategoryId和productName
            Integer productCategoryId = HttpServletRequestUtil.getInt(request, PRODUCT_CATEGORY_ID);
            String productName = HttpServletRequestUtil.getString(request, PRODUCT_NAME);

            try {
                //组合查询条件,根据商店id,商品类别id,商品名字查询
                Product productCondition = compactProductCondition(shopId, productCategoryId, productName);
                //开始分页查询
                PageHelper.startPage(pageNum, pageSize);
                ProductExecution pe = productService.getProductListByPageHelper(productCondition);
                PageInfo info = new PageInfo(pe.getProductList());
                //返回查到的总记录数和商品列表给前段
                modelMap.put(SUCCESS, true);
                modelMap.put(PRODUCT_LIST, pe.getProductList());
                modelMap.put(COUNT, info.getTotal());

            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(SUCCESS, "该商店不在地球");
        }
        return modelMap;
    }

    /**
     * 组合查询条件,根据商店id,商品类别id,商品名字查询查询商品
     *
     * @param shopId
     * @param productCategoryId
     * @param productName
     * @return
     */
    private Product compactProductCondition(Integer shopId, Integer productCategoryId, String productName) {
        Product productCondition = new Product();
        if (shopId > 0) {
            Shop shop = new Shop();
            shop.setShopId(shopId);
            productCondition.setShop(shop);
        }
        if (productCategoryId > 0) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }
        if (productName != null) {
            productCondition.setProductName(productName);
        }
        productCondition.setEnableStatus(1);
        return productCondition;
    }

    /**
     * 获取商店详情信息操作(商品类别)
     *
     * @param request
     * @return
     */
    @GetMapping("/shopDetails")
    @ResponseBody
    private Map<String, Object> shopDetails(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        Integer shopId = HttpServletRequestUtil.getInt(request, SHOP_ID);

        Shop shop = null;
        List<ProductCategory> productCategoryList = null;

        if (shopId != null && shopId > 0) {
            //通过传来的shopId查询指定商店和其下的商品类别
            shop = shopService.getShopById(shopId);
            productCategoryList = productCategoryService.getProductCategoryList(shopId);

            modelMap.put(SUCCESS, true);
            modelMap.put(SHOP, shop);
            modelMap.put(PRODUCT_CATEGORY_LIST, productCategoryList);
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "商店不存在");
        }
        return modelMap;
    }
}
