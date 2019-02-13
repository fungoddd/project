package com.fun.dao;

import com.fun.entity.Award;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FungodApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AwardDaoTest {
    @Autowired
    private AwardMapper awardMapper;

    /**
     * 测试添加奖品信息
     */
    @Test
    @Ignore
    public void testAInsertAward() {
        Award award = new Award();
        award.setAwardName("测试奖品");
        award.setAwardDesc("这是测试奖品");
        award.setCreateTime(new Date());
        award.setLastEditTime(new Date());
        award.setEnableStatus(1);
        award.setPoint(1);
        award.setPriority(1);
        award.setShopId(6);
        int num = awardMapper.insertAward(award);
        assertEquals(1, num);
    }

    /**
     * 测试修改奖品
     */
    @Test
    @Ignore
    public void testBUpdateAward() {
        Award award = new Award();
        award.setShopId(6);
        award.setAwardId(1);
        award.setAwardName("修改测试");
        award.setAwardDesc("这是修改测试");
        award.setLastEditTime(new Date());
        award.setEnableStatus(2);
        award.setPoint(2);
        award.setPriority(2);
        int num = awardMapper.updateAward(award);
        assertEquals(1, num);
    }

    /**
     * 测试查询指定奖品
     */
    @Test
    @Ignore
    public void testCSelectAward() {
        Award award = awardMapper.selectAwardById(1);
        assertEquals("修改测试", award.getAwardName());
        System.out.println("奖品:------" + award.toString());
    }

    @Test
    @Ignore
    public void testDDeleteAward() {
        int num = awardMapper.deleteAward(1, 6);
        assertEquals(1, num);
    }


}
