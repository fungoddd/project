package com.fun.exceptions;

public class ProductCategoryOperationException extends RuntimeException {


    private static final long serialVersionUID = 8389972738752741385L;

    public ProductCategoryOperationException(String msg) {
        super(msg);
    }
}
