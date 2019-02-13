package com.fun.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QRLoginURL {
    /**
     * 跳转到微信扫码登录
     */
    @GetMapping("/QRlogin")
    public String QRlogin() {
        return "local/QRCodeLogin";
    }

}
