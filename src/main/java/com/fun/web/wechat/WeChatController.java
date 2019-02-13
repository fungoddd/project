package com.fun.web.wechat;

import com.fun.util.wechat.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.fun.constant.ControllerConst.*;


/**
 * @Author: FunGod
 * @Date: 2018-12-07 10:40:48
 * @Desc: 微信路由, 响应微信发送的Token验证
 */
@Controller
@RequestMapping("/wechat")
public class WeChatController {
    private static Logger log = LoggerFactory.getLogger(WeChatController.class);

    @GetMapping
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        log.debug("wechat get...");
        // 获取微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
        String signature = request.getParameter(SIGNATURE);
        // 获取时间戳
        String timestamp = request.getParameter(TIME_STAMP);
        // 获取随机数
        String nonce = request.getParameter(NONCE);
        // 获取随机字符串
        String echostr = request.getParameter(ECHO_STR);

        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        PrintWriter out = null;
        try {
            out = response.getWriter();
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                log.debug("wechat get success....");
                out.print(echostr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //如果out为null,关闭输出
            if (out != null)
                out.close();
        }
    }
}
