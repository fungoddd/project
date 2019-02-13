package com.fun.dao;

import com.fun.entity.PersonInfo;
import com.fun.entity.Shop;
import com.fun.entity.UserShopMap;
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
public class UserShopMapDaoTest {
    @Autowired
    private UserShopMapMapper userShopMapMapper;

    /**
     * 测试新增两条顾客在商店的积分记录
     */
    @Test
    @Ignore
    public void testAInsertUserShopMap() {
        UserShopMap userShopMap = new UserShopMap();
        //所在商店
        Shop shop = new Shop();
        shop.setShopId(6);
        shop.setShopName("好哥哥美容美发");
        //顾客
        PersonInfo user = new PersonInfo();
        user.setUserId(1);
        user.setName("小凡");
        userShopMap.setShop(shop);
        userShopMap.setUser(user);
        userShopMap.setCreateTime(new Date());
        userShopMap.setPoint(1);

        int num = userShopMapMapper.insertUserShopMap(userShopMap);
        assertEquals(1, num);
        ///////
        UserShopMap userShopMap2 = new UserShopMap();
        PersonInfo user2 = new PersonInfo();
        user2.setUserId(2);
        user2.setName("小郭");
        userShopMap2.setShop(shop);
        userShopMap2.setUser(user2);
        userShopMap2.setCreateTime(new Date());
        userShopMap2.setPoint(2);

        int num2 = userShopMapMapper.insertUserShopMap(userShopMap2);
        assertEquals(1, num2);
    }

    /**
     * 测试更新顾客在某商店的积分
     */
    @Test
    @Ignore
    public void testBUpdateUserShopMapPoint() {
        UserShopMap userShopMap = new UserShopMap();
        Shop shop = new Shop();
        shop.setShopId(6);
        PersonInfo user = new PersonInfo();
        user.setUserId(1);
        userShopMap.setPoint(3);
        userShopMap.setShop(shop);
        userShopMap.setUser(user);

        int num = userShopMapMapper.updateUserShopMapPoint(userShopMap);
        assertEquals(1, num);
    }

    /**
     * 测试查询
     */
    @Test
    @Ignore
    public void testCSelectUserShopMap() {
        UserShopMap userShopMap = new UserShopMap();
        //查询全部
        List<UserShopMap> userShopMapList = userShopMapMapper.selectUserShopMapList(userShopMap);
        //商店名查询
        Shop shop = new Shop();
        shop.setShopName("好哥哥美容美发");
        userShopMap.setShop(shop);
        List<UserShopMap> userShopMapList1 = userShopMapMapper.selectUserShopMapList(userShopMap);
        //用户名查询
        PersonInfo user = new PersonInfo();
        user.setName("小凡");
        userShopMap.setUser(user);
        List<UserShopMap> userShopMapList2 = userShopMapMapper.selectUserShopMapList(userShopMap);
        //按时间范围查询
        userShopMap.setCreateTime(new Date());
        List<UserShopMap> userShopMapList3 = userShopMapMapper.selectUserShopMapList(userShopMap);
        //查询某用户在某商店的积分信息
        UserShopMap userShopMap1 = userShopMapMapper.selectUserShopMapById(1, 6);
    }

}
