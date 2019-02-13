package com.fun.exceptions;

public class ProductOperationException extends RuntimeException {

    private static final long serialVersionUID = -3390995254878761504L;

    public ProductOperationException(String msg) {
        super(msg);
    }
}
