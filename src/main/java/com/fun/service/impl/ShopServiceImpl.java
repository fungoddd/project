package com.fun.service.impl;

import com.fun.dao.ShopAuthMapMapper;
import com.fun.dao.ShopMapper;
import com.fun.dto.shop.ShopExecution;
import com.fun.entity.Shop;
import com.fun.entity.ShopAuthMap;
import com.fun.enums.ShopStateEnum;
import com.fun.exceptions.ShopOperationException;
import com.fun.service.ShopService;
import com.fun.util.Image.ImageHelper;
import com.fun.util.Image.ImageUtil;
import com.fun.util.Image.PathUtil;
import com.fun.util.page.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-02 01:27:26
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopAuthMapMapper shopAuthMapMapper;


    /**
     * PageHelper分页查询店铺
     *
     * @param shopCondition
     * @return
     */
    @Override
    public ShopExecution selectShopListByPageHelper(Shop shopCondition) {

        List<Shop> shopList = shopMapper.getShopList(shopCondition);

        ShopExecution se = new ShopExecution();

        if (shopList != null && shopList.size() > 0) {
            se.setShopList(shopList);

        } else {
            se.setShopList(null);
            se.setState(ShopStateEnum.NULL_SHOP.getState());
        }

        return se;
    }

    /**
     * 查询店铺列表进行分页(多条件查询)
     *
     * @param 暂时放弃使用//shopCondition
     * @param pageIndex             从第几页获取数据
     * @param pageSize              每页返回条数
     * @return
     */
   /* @Override
    public ShopExecution selectShopList(Shop shopCondition, int pageIndex, int pageSize) {

        //进行分页处理
        int rowIndex = PageUtil.calculateRowIndex(pageIndex, pageSize);

        //查询到的记录
        List<Shop> shopList = shopMapper.selectShopList(shopCondition, rowIndex, pageSize);

        //查到的记录总条数
        int count = shopMapper.selectShopCount(shopCondition);

        ShopExecution se = new ShopExecution();

        if (shopList != null) {

            se.setShopList(shopList);
            se.setCount(count);

        } else {
            se.setState(ShopStateEnum.INNER_ERROR.getState());
        }
        return se;
    }*/


    /**
     * 通过店铺id获取店铺信息
     *
     * @param id
     * @return
     */
    @Override
    public Shop getShopById(Integer id) {

        return shopMapper.selectShopById(id);

    }

    /**
     * 修改商店信息包括图片处理
     *
     * @param shop
     * @param thumbnail 商品缩略图 ImageHelper把shopImgInputStream fileName封装起来
     * @return ShopExecution
     * @throws ShopOperationException
     */
    @Transactional
    @Override
    public ShopExecution updateShop(Shop shop, ImageHelper thumbnail) throws ShopOperationException {

        if (shop == null || shop.getShopId() == null) {

            return new ShopExecution(ShopStateEnum.NULL_SHOP);

        } else {
            try {

                //1.判断是否需要处理图片
                if (thumbnail != null) {

                    //通过id查询店铺得到一个对象
                    Shop tempShop = shopMapper.selectShopById(shop.getShopId());

                    //如果当前店铺对象中的图片不为空
                    if (tempShop.getShopImg() != null) {
                        //进行图片文件目录的删除
                        ImageUtil.deleteFileOrPath(tempShop.getShopImg());
                    }
                    //添加新的图片
                    addShopImg(shop, thumbnail);
                }

                //2.更新商店信息
                shop.setLastEditTime(new Date());

                int effectedNum = shopMapper.updateShop(shop);

                if (effectedNum <= 0) {
                    return new ShopExecution(ShopStateEnum.INNER_ERROR);

                } else {
                    //为了信息真实同步获取最新的更改后的数据库中的shop得到shop对象
                    shop = shopMapper.selectShopById(shop.getShopId());
                    return new ShopExecution(ShopStateEnum.SUCCESS, shop);
                }

            } catch (Exception e) {
                throw new ShopOperationException("updateShop error:" + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的优点： 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
     * //1.插入店铺信息-》2.返回店铺Id-》3.根据店铺Id创建存储图片的文件夹-》4.把文件夹的地址更新回店铺信息中
     */
    /**
     * 注册商店信息包括图片处理
     *
     * @param shopAdmin
     * @param thumbnail 商品缩略图 ImageHelper把shopImgInputStream fileName封装起来
     * @return ShopExecution
     * @throws ShopOperationException
     */
    public ShopExecution addShop(Shop shop, ImageHelper thumbnail) throws ShopOperationException {

        if (shop == null) {
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }

        try {

            //设置店铺初始状态为0,审核中
            shop.setEnableStatus(0);

            //创建时间
            shop.setCreateTime(new Date());

            //最后操作时间
            shop.setLastEditTime(new Date());

            //权重默认为0
            shop.setPriority(0);

            //添加店铺信息
            int effectedNum = shopMapper.insertShop(shop);

            //如果添加返回值<0创建失败
            if (effectedNum <= 0) {
                throw new ShopOperationException("店铺创建失败");

            } else {
                //如果图片不为空
                if (thumbnail.getImage() != null) {

                    try {
                        //存储图片
                        addShopImg(shop, thumbnail);

                    } catch (Exception e) {
                        //添加图片异常
                        throw new ShopOperationException("addShopImg error:" + e.getMessage());
                    }
                    //更新店铺的图片地址
                    effectedNum = shopMapper.updateShop(shop);

                    //如果更新失败抛出异常
                    if (effectedNum <= 0) {
                        throw new ShopOperationException("添加缩略图失败");
                    }
                    //添加商店的员工授权信息,创建商店时也创建店家本人授权信息
                    ShopAuthMap shopAuthMap = new ShopAuthMap();
                    shopAuthMap.setEmployee(shop.getOwner());
                    shopAuthMap.setTitle("老板");
                    shopAuthMap.setTitleFlag(0);
                    shopAuthMap.setEnableStatus(1);
                    shopAuthMap.setCreateTime(new Date());
                    shopAuthMap.setLastEditTime(new Date());
                    shopAuthMap.setShop(shop);
                    try {
                        int effectNum = shopAuthMapMapper.insertShopAuthMap(shopAuthMap);
                        if (effectNum <= 0) {
                            throw new ShopOperationException("添加商店授权失败");
                        }
                    } catch (Exception e) {
                        throw new ShopOperationException("insertShopAuthMap error:" + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            throw new ShopOperationException("insertShop error: " + e.getMessage());
        }
        //添加成功返回添加成功审核中的shop对象
        return new ShopExecution(ShopStateEnum.CHECK, shop);
    }

    /**
     * 将店铺图片添加到用户相对的文件夹下，并且将店铺信息中店铺图片的地址更新
     *
     * @param shop
     * @param thumbnail
     */
    private void addShopImg(Shop shop, ImageHelper thumbnail) {

        //获得店铺下的图片子路径
        String relativeImgPath = PathUtil.getShopImgPath(shop.getShopId());

        //图片真实路径,传入图片文件流和图片子路径
        String realRelativeImgPath = ImageUtil.generateThumbnail(thumbnail, relativeImgPath);

        //店铺设置图片
        shop.setShopImg(realRelativeImgPath);
    }
}
