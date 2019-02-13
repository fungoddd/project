package com.fun.exceptions;

/**
 * @Author: FunGod
 * @Date: 2018-12-08 01:16:56
 *
 */
public class WechatAuthOpeartionException extends RuntimeException {

    private static final long serialVersionUID = -7541559886471550725L;

    public WechatAuthOpeartionException(String msg) {
        super(msg);
    }
}
