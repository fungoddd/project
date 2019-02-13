package com.fun.dto.product;

import com.fun.entity.Product;
import com.fun.enums.ProductStateEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 12:00:51
 * @Desc: 商品对象的接收处理
 */
@Getter
@Setter
public class ProductExecution {
    //结果状态
    private int state;

    //标识状态
    private String stateInfo;

    //商品数量
    private int count;

    //要操作的商品(crud)
    private Product product;

    //商品列表
    private List<Product> productList;

    public ProductExecution() {
    }

    //操作失败的构造函数
    public ProductExecution(ProductStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    //操作成功的构造函数
    public ProductExecution(ProductStateEnum stateEnum, Product product) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.product = product;
    }

    //操作成功的构造函数
    public ProductExecution(ProductStateEnum stateEnum, List<Product> productList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.productList = productList;
    }
}
