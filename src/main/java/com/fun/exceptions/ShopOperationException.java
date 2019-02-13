package com.fun.exceptions;

public class ShopOperationException extends RuntimeException {
    //添加序列化id
    private static final long serialVersionUID = -5278647456466933925L;
    //异常处理
    public ShopOperationException(String msg) {
        super(msg);
    }
}
