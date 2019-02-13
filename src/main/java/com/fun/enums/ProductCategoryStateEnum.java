package com.fun.enums;

import lombok.Getter;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 12:05:56
 * @Desc: 商品类别枚举信息,返回结果状态的封装处理
 */
@Getter
public enum ProductCategoryStateEnum {

    SUCCESS(1, "操作成功"),
    INNER_ERROR(-1001, "操作失败"),
    EMPTY_LIST(-1002, "添加数至少为1");
    //结果状态
    private int state;
    //标识状态
    private String stateInfo;

    private ProductCategoryStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    /**
     * 根据传入的state返回相应的enum值
     */
    public static ProductCategoryStateEnum stateOf(int state) {
        for (ProductCategoryStateEnum productCategoryStateEnum : values()) {
            if (productCategoryStateEnum.getStateInfo() == null) {
                return productCategoryStateEnum;
            }
        }
        return null;
    }

}
