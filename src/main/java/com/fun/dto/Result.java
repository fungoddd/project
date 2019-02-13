package com.fun.dto;

import lombok.Data;


/**
 * @Author: FunGod
 * @Date: 2018-12-07 12:01:22
 * @Desc: 封装Json对象,返回结果时候使用
 * @param <T>
 */
@Data
public class Result<T> {
    private boolean success;//是否返回成功标志
    private T data;//返回成功时返回的数据
    private String errMsg;//错误信息
    private int errCode;//状态码

    public Result() {
    }

    //返回成功的构造函数
    public Result(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    //返回失败的构造函数
    public Result(boolean success, String errMsg, int errCode) {
        this.success = success;
        this.errMsg = errMsg;
        this.errCode = errCode;
    }
}
