package com.fun.dao;

import com.fun.entity.PersonInfo;
import com.fun.entity.Product;
import com.fun.entity.Shop;
import com.fun.entity.UserProductMap;
import com.fun.main.FungodApplication;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.omg.CORBA.ORB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FungodApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserProductMapDao {
    @Autowired
    private UserProductMapMapper userProductMapMapper;

    /**
     * 测试添加顾客消费商品的映射
     */
    @Test
    @Ignore
    public void testAInsertUserProductMap() {
        UserProductMap userProductMap = new UserProductMap();
        //消费时间
        userProductMap.setCreateTime(new Date());
        //消费商品获得的积分
        userProductMap.setPoint(2);
        //所属商店
        Shop shop = new Shop();
        shop.setShopId(6);
        shop.setShopName("好哥哥美容美发");
        //商品
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("店长给您剃光头");
        //消费的顾客
        PersonInfo user = new PersonInfo();
        user.setUserId(1);
        user.setName("小凡");
        //操作员
        PersonInfo operator = new PersonInfo();
        operator.setUserId(2);

        userProductMap.setShop(shop);
        userProductMap.setProduct(product);
        userProductMap.setUser(user);
        userProductMap.setOperator(operator);

        int num = userProductMapMapper.insertUserProductMap(userProductMap);
        assertEquals(1, num);


        ////////////////
        UserProductMap userProductMap2 = new UserProductMap();
        PersonInfo user2 = new PersonInfo();
        user2.setUserId(2);
        user2.setName("小郭");
        userProductMap2.setCreateTime(new Date());
        userProductMap2.setPoint(5);
        userProductMap2.setShop(shop);
        userProductMap2.setProduct(product);
        userProductMap2.setUser(user2);
        userProductMap2.setOperator(user);

        int num2 = userProductMapMapper.insertUserProductMap(userProductMap2);
        assertEquals(1, num2);


    }

    /**
     * 测试查询顾客消费商品的映射列表
     */
    @Test
    @Ignore
    public void testBSelectUserProductMap() {
        UserProductMap userProductMap = new UserProductMap();
        //通过商店查询
        Shop shop = new Shop();
        shop.setShopId(6);
        userProductMap.setShop(shop);
        List<UserProductMap> userProductMapList = userProductMapMapper.selectUserProductMapList(userProductMap);
        assertEquals(2, userProductMapList.size());

        UserProductMap userProductMap2 = new UserProductMap();
        //通过商店查询
        Shop shop2 = new Shop();
        shop.setShopName("好哥哥美容美发");
        userProductMap2.setShop(shop2);
        //通过商店名字查询
        List<UserProductMap> userProductMapList2 = userProductMapMapper.selectUserProductMapList(userProductMap2);
        assertEquals("好哥哥美容美发", userProductMapList2.get(0).getShop().getShopName());
        //通过商品名字
        Product product = new Product();
        product.setProductName("店长给您剃光头");
        userProductMap2.setProduct(product);
        List<UserProductMap> userProductMapList3 = userProductMapMapper.selectUserProductMapList(userProductMap2);
        assertEquals("店长给您剃光头", userProductMapList3.get(0).getProduct().getProductName());
    }
}
