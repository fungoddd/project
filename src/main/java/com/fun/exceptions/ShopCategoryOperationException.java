package com.fun.exceptions;

public class ShopCategoryOperationException extends RuntimeException {


    private static final long serialVersionUID = 1325667785175933918L;

    public ShopCategoryOperationException(String msg) {
        super(msg);
    }
}
