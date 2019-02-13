package com.fun.dto.wechat;

import lombok.Data;

/**
 * @Author: FunGod
 * @Date: 2018-12-13 23:58:10
 * @Desc: 用于接收平台二维码信息
 */
@Data
public class WeChatInfo {
    //用户
    private Integer customerId;
    //商品
    private Integer productId;
    //领取奖品
    private Integer userAwardId;
    //创建世界
    private Long createTime;
    //商店
    private Integer shopId;
}
