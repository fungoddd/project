package com.fun.exceptions;

public class LocalAuthOperationException extends RuntimeException {

    private static final long serialVersionUID = -7591808393729225711L;

    public LocalAuthOperationException(String msg) {
        super(msg);
    }
}
