package com.fun.dao;

import com.fun.entity.Award;
import com.fun.entity.PersonInfo;
import com.fun.entity.Shop;
import com.fun.entity.UserAwardMap;
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
public class UserAwardMapDao {
    @Autowired
    private UserAwardMapMapper userAwardMapMapper;

    /**
     * 测试添加顾客领取奖品的映射
     */
    @Test
    @Ignore
    public void testAInsertUserAwardMap() {
        UserAwardMap userAwardMap = new UserAwardMap();
        //所属商店
        Shop shop = new Shop();
        shop.setShopId(12);
        //奖品实例
        Award award = new Award();
        award.setAwardId(1);
        award.setAwardName("测试奖品");
        //顾客
        PersonInfo user = new PersonInfo();
        user.setUserId(2);
        user.setName("小郭");
        //操作人
        PersonInfo operator = new PersonInfo();
        operator.setUserId(1);
        operator.setName("小凡");

        userAwardMap.setShop(shop);
        userAwardMap.setAward(award);
        userAwardMap.setUser(user);
        userAwardMap.setOperator(operator);
        userAwardMap.setCreateTime(new Date());
        userAwardMap.setUsedStatus(0);
        userAwardMap.setPoint(2);

        int num = userAwardMapMapper.insertUserAwardMap(userAwardMap);
        assertEquals(1, num);

    }

    /**
     * 测试修改
     */
    @Test
    @Ignore
    public void testBUpdate() {
        UserAwardMap userAwardMap = new UserAwardMap();
        //主键
        userAwardMap.setUserAwardId(1);
        //状态
        userAwardMap.setUsedStatus(0);
        //用户
        PersonInfo user = new PersonInfo();
        user.setUserId(1);
        userAwardMap.setUser(user);

        int num = userAwardMapMapper.updateUserAwardMap(userAwardMap);
        assertEquals(1, num);

    }

    /**
     * 测试查询顾客兑换奖品列表
     */
    @Test
    @Ignore
    public void testCSelectUserAwardMapList() {
        UserAwardMap userAwardMap = new UserAwardMap();
        //通过奖品名查询
        Award award = new Award();
        award.setAwardName("测试奖品");
        userAwardMap.setAward(award);
        List<UserAwardMap> userAwardMapList = userAwardMapMapper.selectUserAwardList(userAwardMap);
        System.out.println("操作员名字" + userAwardMapList.get(0).getOperator().getName());
        //通过过客名字查询
        PersonInfo user=new PersonInfo();
        user.setName("小郭");
        userAwardMap.setUser(user);
        List<UserAwardMap> userAwardMapList2 = userAwardMapMapper.selectUserAwardList(userAwardMap);
        System.out.println("顾客名字" + userAwardMapList2.get(0).getUser().getName());

    }

}
