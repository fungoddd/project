package com.fun.dto.productCategory;

import com.fun.entity.ProductCategory;
import com.fun.enums.ProductCategoryStateEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 12:00:21
 * @Desc: 商品类别对象的接收处理
 */
@Getter
@Setter
public class ProductCategoryExecution {
    //结果状态
    private int state;

    //标识状态
    private String stateInfo;


    //商品类别列表
    private List<ProductCategory> productCategoryList;

    public ProductCategoryExecution() {
    }

    //操作失败
    public ProductCategoryExecution(ProductCategoryStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    //操作成功
    public ProductCategoryExecution(ProductCategoryStateEnum stateEnum, List<ProductCategory> productCategoryList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.productCategoryList = productCategoryList;
    }
}
