package com.fun.web.front;

import com.fun.dto.shop.ShopExecution;
import com.fun.entity.Area;
import com.fun.entity.Shop;
import com.fun.entity.ShopCategory;
import com.fun.service.AreaService;
import com.fun.service.ShopCategoryService;
import com.fun.service.ShopService;
import com.fun.util.request.HttpServletRequestUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-03 15:07:58
 * @Desc: 获取商店列表()
 */
@RestController
@RequestMapping("/front")
public class ShopListController {

    @Autowired
    private AreaService areaService;

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Autowired
    private ShopService shopService;

    /**
     * 获取商店列表页里的商店类别列表(一级二级)以及区域信息
     *
     * @param request
     * @return
     */
    @GetMapping("/shopListInfo")
    @ResponseBody
    private Map<String, Object> shopListInfo(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        Integer parentId = HttpServletRequestUtil.getInt(request, PARENT_ID);

        ShopCategory shopCategory = new ShopCategory();
        if (parentId > 0) {

            //如果parentId存在取出一级商品类别下的二级商品类别
            shopCategory.setParentId(parentId);

        } else {
            // 如果parentId不存在，则取出所有一级ShopCategory（用户在首页选择的是全部商品列表的时候）
            shopCategory = null;
        }
        try {
            //通过封装好的商品类别对象,查询,得到商类别列表
            List<ShopCategory> shopCategoryList = shopCategoryService.getShopCategoryList(shopCategory);
            modelMap.put(SUCCESS, true);
            modelMap.put(SHOP_CATEGORY_LIST, shopCategoryList);

        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }
        try {
            //获取区域列表
            List<Area> areaList = areaService.getAreaList();
            modelMap.put(AREA_LIST, areaList);

        } catch (Exception e) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, e.toString());
            return modelMap;
        }

        return modelMap;
    }

    /**
     * 获取指定查询条件下的店铺(多条件高级查询)
     *
     * @param request
     * @return
     */
    @GetMapping("/shops")
    @ResponseBody
    private Map<String, Object> shops(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        //获取前端传来的页码
        int pageNum = HttpServletRequestUtil.getInt(request, PAGE_INDEX);
        //获取前端传来的每页需要显示记录数
        int pageSize = HttpServletRequestUtil.getInt(request, PAGE_SIZE);
        if (pageNum >= 0 && pageSize >= 0) {
            System.out.println("当前页码:++++++++++" + pageNum);
            //获取一级类别
            Integer parentId = HttpServletRequestUtil.getInt(request, PARENT_ID);
            //获取二级类别
            Integer shopCategoryId = HttpServletRequestUtil.getInt(request, SHOP_CATEGORY_ID);
            //获取区域id
            Integer areaId = HttpServletRequestUtil.getInt(request, AREA_ID);
            //获取输入商店名字搜索
            String shopName = HttpServletRequestUtil.getString(request, SHOP_NAME);
            //组合上面的查询条件
            Shop shopCondition = compactShopConditionSearch(parentId, shopCategoryId, areaId, shopName);

            try {
                //开始分页,传入筛选过查询条件的商品对象进行查询
                PageHelper.startPage(pageNum, pageSize);
                ShopExecution se = shopService.selectShopListByPageHelper(shopCondition);
                //PageInfo<Shop> info = new PageInfo<>(se.getShopList(), pageNum);
                PageInfo info = new PageInfo(se.getShopList());
                modelMap.put(SUCCESS, true);
                modelMap.put(SHOP_LIST, se.getShopList());
                //返回查询到的记录条数
                modelMap.put(COUNT, info.getTotal());
                //modelMap.put("getPages", info.getPages());
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }

        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "PageSize or PageNum warning!");
        }
        return modelMap;
    }

/*******************************************/
    /**
     * 组合查询条件
     *
     * @param parentId
     * @param shopCategoryId
     * @param areaId
     * @param shopName
     * @return
     */
    private Shop compactShopConditionSearch(Integer parentId, Integer shopCategoryId, Integer areaId, String shopName) {
        Shop shopCondition = new Shop();
        if (parentId > 0) {
            // 查询某个一级ShopCategory下面的所有二级ShopCategory里面的商店列表
            ShopCategory shopCategory = new ShopCategory();
            shopCategory.setParentId(parentId);
            shopCondition.setShopCategory(shopCategory);
        }
        if (shopCategoryId > 0) {
            // 查询某个二级ShopCategory下面的店铺列表
            ShopCategory shopCategory = new ShopCategory();
            shopCategory.setShopCategoryId(shopCategoryId);
            shopCondition.setShopCategory(shopCategory);
        }
        if (areaId > 0) {
            // 查询位于某个区域id下的店铺列表
            Area area = new Area();
            area.setAreaId(areaId);
            shopCondition.setArea(area);
        }
        if (shopName != null && !shopName.equals("")) {
            shopCondition.setShopName(shopName);
        }
        //TODO setStatus(1);设置查找审核通过的
        return shopCondition;
    }
}
