package com.fun.web.localUser;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/localAuth")
public class LocalURLController {
    /**
     * 跳转到绑定本地账号页面
     *
     * @return
     */
    @GetMapping("/bindLocalAuth")
    public String bindLocalAuth() {
        return "local/bindLocalAuth";
    }

    /**
     * 跳转到修改密码路由
     *
     * @return
     */
    @GetMapping("/changePwd")
    public String changePwd() {
        return "local/changePwd";
    }

    /**
     * 跳转到登录页
     *
     * @return
     */
    @GetMapping("/login")
    public String login() {
        return "local/login";
    }
}
