package com.fun.exceptions;

public class UserAwardMapOperationException extends RuntimeException {
    private static final long serialVersionUID = 5350171812238889707L;

    public UserAwardMapOperationException(String msg) {
        super(msg);
    }
}
