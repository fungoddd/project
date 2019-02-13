package com.fun.service.impl;

import com.fun.dao.ShopAuthMapMapper;
import com.fun.dto.shopAuthMap.ShopAuthMapExecution;
import com.fun.entity.ShopAuthMap;
import com.fun.enums.ShopAuthMapStateEnum;
import com.fun.exceptions.ShopAuthMapOperationException;
import com.fun.service.ShopAuthMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-13 19:06:50
 * @Desc: 商店员工授权信息业务层实现
 */
@Service
public class ShopAuthMapServiceImpl implements ShopAuthMapService {

    @Autowired
    private ShopAuthMapMapper shopAuthMapMapper;

    /**
     * 通过商店id获取该商店对员工的授权信息列表
     *
     * @param shopId
     * @return
     */
    @Override
    public ShopAuthMapExecution getShopAuthMapListByShopId(Integer shopId) {

        if (shopId != null && shopId > 0) {

            List<ShopAuthMap> shopAuthMapList = shopAuthMapMapper.selectShopAuthMapListByShopId(shopId);

            ShopAuthMapExecution sae = new ShopAuthMapExecution();

            sae.setShopAuthMapList(shopAuthMapList);

            return sae;

        } else {
            return null;
        }
    }

    /**
     * 通过主键获取指定的商店授权信息
     *
     * @param shopAuthId
     * @return
     */
    @Override
    public ShopAuthMap getShopAuthMapById(Integer shopAuthId) {
        return shopAuthMapMapper.selectShopAuthMapById(shopAuthId);
    }

    /**
     * 添加商店授权信息
     *
     * @param shopAuthMap
     * @return
     * @throws ShopAuthMapOperationException
     */
    @Override
    @Transactional
    public ShopAuthMapExecution addShopAuthMap(ShopAuthMap shopAuthMap) throws ShopAuthMapOperationException {

        if (shopAuthMap != null && shopAuthMap.getShop() != null && shopAuthMap.getShop().getShopId() != null && shopAuthMap.getEmployee() != null
                && shopAuthMap.getEmployee().getUserId() != null) {
            //添加初始信息
            shopAuthMap.setCreateTime(new Date());
            shopAuthMap.setLastEditTime(new Date());
            //默认权限可用
            shopAuthMap.setEnableStatus(1);


            try {
                int effectNum = shopAuthMapMapper.insertShopAuthMap(shopAuthMap);

                if (effectNum <= 0) {
                    throw new ShopAuthMapOperationException("添加授权信息失败");
                }
                return new ShopAuthMapExecution(ShopAuthMapStateEnum.SUCCESS, shopAuthMap);

            } catch (Exception e) {
                throw new ShopAuthMapOperationException("insertShopAuthMap error:" + e.toString());
            }
        } else {
            return new ShopAuthMapExecution(ShopAuthMapStateEnum.NULL_SHOPAUTH_INFO);
        }
    }

    /**
     * 更新商店授权信息(职称,职称flag,可用状态)
     *
     * @param shopAuthMap
     * @return
     * @throws ShopAuthMapOperationException
     */
    @Override
    @Transactional
    public ShopAuthMapExecution updateShopAuthMap(ShopAuthMap shopAuthMap) throws ShopAuthMapOperationException {

        if (shopAuthMap == null || shopAuthMap.getShopAuthId() == null) {

            return new ShopAuthMapExecution(ShopAuthMapStateEnum.NULL_SHOPAUTH_INFO);

        } else {

            try {
                int effectNum = shopAuthMapMapper.updateShopAuthMap(shopAuthMap);

                if (effectNum <= 0) {
                    throw new ShopAuthMapOperationException("修改授权信息失败");
                }
                return new ShopAuthMapExecution(ShopAuthMapStateEnum.SUCCESS, shopAuthMap);

            } catch (Exception e) {
                throw new ShopAuthMapOperationException("updateShopAuthMap error:" + e.toString());
            }
        }
    }

    /**
     * 删除指定商店授权信息
     *
     * @param shopAuthId
     * @return
     * @throws ShopAuthMapOperationException
     */
    @Override
    @Transactional
    public ShopAuthMapExecution removeShopAuthMap(Integer shopAuthId, Integer shopId) throws ShopAuthMapOperationException {
        if (shopAuthId == null || shopAuthId <= 0 || shopId == null || shopId <= 0) {
            return new ShopAuthMapExecution(ShopAuthMapStateEnum.NULL_SHOPAUTH_ID);
        } else {
            try {
                int effectNum = shopAuthMapMapper.deleteShopAuthMap(shopAuthId, shopId);
                if (effectNum <= 0) {
                    throw new ShopAuthMapOperationException("删除授权信息失败");
                }
                return new ShopAuthMapExecution(ShopAuthMapStateEnum.SUCCESS);
            } catch (Exception e) {
                throw new ShopAuthMapOperationException("removeShopAuthMap error:" + e.toString());
            }
        }

    }
}
