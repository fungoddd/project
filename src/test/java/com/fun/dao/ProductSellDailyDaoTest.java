package com.fun.dao;

import com.fun.entity.ProductSellDaily;
import com.fun.entity.Shop;
import com.fun.main.FungodApplication;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FungodApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductSellDailyDaoTest {

    @Autowired
    private ProductSellDailyMapper productSellDailyMapper;

    /**
     * 测试统计销量
     */
    @Test
    @Ignore
    public void testAInsertProductSellDaily() {
        //创建商品日销量统计
        int num = productSellDailyMapper.insertProductSellDaily();
        assertEquals(1, num);
    }
    /**
     * 测试添加默认统计
     */
    @Test
    @Ignore
    public void testBInsertDefaultProductSellDaily() {
        //创建商品日销量统计
        int num = productSellDailyMapper.insertDefaultProductSellDaily();
        assertEquals(1, num);
    }

    /**
     * 测试查询商品销量列表
     */
    @Test
    @Ignore
    public void testBSelectProductSellDaily() {
        ProductSellDaily productSellDaily = new ProductSellDaily();
        Shop shop = new Shop();
        shop.setShopId(6);
        //通过商店查询销量
        productSellDaily.setShop(shop);
        List<ProductSellDaily> productSellDailyList = productSellDailyMapper.selectProductSellDaily
                (productSellDaily, null, null);

        assertEquals(1, productSellDailyList.size());
        assertEquals("店长给您剃光头", productSellDailyList.get(0).getProduct().getProductName());


    }
}
