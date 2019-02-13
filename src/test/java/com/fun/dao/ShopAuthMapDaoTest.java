package com.fun.dao;

import com.fun.entity.PersonInfo;
import com.fun.entity.Shop;
import com.fun.entity.ShopAuthMap;
import com.fun.main.FungodApplication;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FungodApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShopAuthMapDaoTest {
    @Autowired
    private ShopAuthMapMapper shopAuthMapMapper;

    /**
     * 测试新增商店员工授权信息
     */
    @Test
    @Ignore
    public void testAInsertShopAuthMap() {
        ShopAuthMap shopAuthMap = new ShopAuthMap();
        PersonInfo employee = new PersonInfo();
        employee.setUserId(2);
        employee.setName("小郭");
        shopAuthMap.setEmployee(employee);
        shopAuthMap.setCreateTime(new Date());
        shopAuthMap.setLastEditTime(new Date());
        shopAuthMap.setEnableStatus(1);
        Shop shop = new Shop();
        shop.setShopId(6);
        shopAuthMap.setShop(shop);
        shopAuthMap.setTitle("老板娘");
        shopAuthMap.setTitleFlag(1);
        int num = shopAuthMapMapper.insertShopAuthMap(shopAuthMap);
        assertEquals(1, num);

    }

    /**
     * 测试更新商店对员工授权信息
     */
    @Test
    @Ignore
    public void testBUpdateShopAuthMap() {
        ShopAuthMap shopAuthMap = new ShopAuthMap();
        shopAuthMap.setTitleFlag(2);
        shopAuthMap.setLastEditTime(new Date());

        int num = shopAuthMapMapper.updateShopAuthMap(shopAuthMap);
        assertEquals(1, num);
    }

    @Test
    @Ignore
    public void testCSelectShopAuthMap() {
        ShopAuthMap shopAuthMap = shopAuthMapMapper.selectShopAuthMapById(1);
        System.out.println("名字" + shopAuthMap.getName());
        System.out.println("名字" + shopAuthMap.getEmployee().getName());
        //查询所有
        List<ShopAuthMap>shopAuthMapList=shopAuthMapMapper.selectShopAuthMapListByShopId(6);
        System.out.println(shopAuthMapList.get(0).getEmployee().getName());
    }

}
