package com.fun.web.shopAdmin;

import com.fun.dto.Result;
import com.fun.dto.productCategory.ProductCategoryExecution;
import com.fun.entity.ProductCategory;
import com.fun.entity.Shop;
import com.fun.enums.ProductCategoryStateEnum;
import com.fun.exceptions.ProductCategoryOperationException;
import com.fun.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 10:37:46
 * @Desc: 商品类别管理
 */

@Controller
@RequestMapping("/shopAdmin")
public class ProductCategoryManagement {
    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 删除指定商店下的商品类别
     *
     * @param productCategoryId
     * @param request
     * @return
     */
    @PostMapping("/removeProductCategory")
    @ResponseBody
    public Map<String, Object> removeProductCategory(Integer productCategoryId, HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        if (productCategoryId != null && productCategoryId > 0) {
            try {
                Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);

                if (curShop == null) {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, "非法操作!");
                    return modelMap;
                }

                ProductCategoryExecution pce =
                        productCategoryService.removeProductCategory(productCategoryId, curShop.getShopId());

                if (pce.getState() == ProductCategoryStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);
                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, pce.getStateInfo());
                }

            } catch (ProductCategoryOperationException e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }

        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "该类别不存在!");
        }

        return modelMap;
    }

    /**
     * 批量添加商品类别
     *
     * @param productCategoryList
     * @param request
     * @return
     */
    @PostMapping("/addProductCategories")
    @ResponseBody
    private Map<String, Object> addProductCategories(@RequestBody List<ProductCategory> productCategoryList, HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //尽量不用前台传来的数据不安全,从session中获取
        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);

        if (curShop == null) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请选择商店在进行添加");
            return modelMap;
        }
        //foreach遍历给商品类别添加商店id
        for (ProductCategory productCategory : productCategoryList) {

            productCategory.setShopId(curShop.getShopId());
            productCategory.setCreateTime(new Date());
            productCategory.setLastEditTime(new Date());
        }
        if (productCategoryList != null && productCategoryList.size() > 0) {
            try {
                ProductCategoryExecution pce = productCategoryService.batchAddProductCategory(productCategoryList);
                //如果当前操作得到的结果是service层得到的成功状态
                if (pce.getState() == ProductCategoryStateEnum.SUCCESS.getState()) {

                    modelMap.put(SUCCESS, true);

                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, pce.getStateInfo());
                }

            } catch (ProductCategoryOperationException e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }

        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请至少选择一个商品类别删除^_^");
        }

        return modelMap;
    }

    /**
     * 获得指定商店下的所有商品分类,用自定义Result封装
     *
     * @param request
     * @return
     */
    @GetMapping("/getProductCategoryList")
    @ResponseBody
    public Result<List<ProductCategory>> getProductCategory(HttpServletRequest request) {

        Shop curShop = (Shop) request.getSession().getAttribute(CURRENT_SHOP);

        List<ProductCategory> productCategoryList = null;

        if (curShop != null && curShop.getShopId() > 0) {
            productCategoryList = productCategoryService.getProductCategoryList(curShop.getShopId());
            return new Result<List<ProductCategory>>(true, productCategoryList);

        } else {
            ProductCategoryStateEnum pse = ProductCategoryStateEnum.INNER_ERROR;
            return new Result<List<ProductCategory>>(false, pse.getStateInfo(), pse.getState());
        }
    }
}
