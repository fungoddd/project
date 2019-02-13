package com.fun.exceptions;

public class AreaOperationException extends RuntimeException {

    private static final long serialVersionUID = 7632435487589632306L;

    public AreaOperationException(String msg) {
        super(msg);
    }
}
