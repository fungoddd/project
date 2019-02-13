package com.fun.exceptions;

public class AwardOperationException extends RuntimeException {

    private static final long serialVersionUID = 7121030933941690065L;

    public AwardOperationException(String msg) {
        super(msg);
    }
}
