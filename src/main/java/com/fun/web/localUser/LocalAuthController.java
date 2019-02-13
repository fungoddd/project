package com.fun.web.localUser;

import com.fun.dto.localAuth.LocalAuthExecution;
import com.fun.entity.LocalAuth;
import com.fun.entity.PersonInfo;
import com.fun.enums.LocalAuthStateEnum;
import com.fun.service.LocalAuthService;
import com.fun.util.request.HttpServletRequestUtil;
import com.fun.util.verifycode.VerifyCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.fun.constant.ControllerConst.*;

/**
 * @Author: FunGod
 * @Date: 2018-12-09 17:01:15
 * @Desc: 本地账户用户操作
 */
@Controller
@RequestMapping("/localAuth")
public class LocalAuthController {

    @Autowired
    private LocalAuthService localAuthService;

    @PostMapping("/getSession")
    @ResponseBody
    private Map<String, Object> getSession(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        PersonInfo personInfo = (PersonInfo) request.getSession().getAttribute(USER);
        if (personInfo != null && personInfo.getUserId() > 0) {
            modelMap.put(SUCCESS, true);
            modelMap.put("personInfo", personInfo);
        } else {
            modelMap.put(SUCCESS, false);
        }
        return modelMap;
    }

    /**
     * 微信用户绑定平台账号
     *
     * @param request
     * @return
     */
    @PostMapping("/bindLocalAuth")
    @ResponseBody
    private Map<String, Object> bindLocalAuth(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误");
            return modelMap;
        }
        //获取用户输入的账号
        String username = HttpServletRequestUtil.getString(request, USERNAME);
        //获取用户输入的密码
        String password = HttpServletRequestUtil.getString(request, PASSWORD);
        //session中获取用户信息userId
        PersonInfo personInfo = (PersonInfo) request.getSession().getAttribute(USER);
        if (personInfo == null) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请先登录在进行操作");
            return modelMap;
        }
        Integer userId = personInfo.getUserId();
        //判断条件
        if (username != null || password != null && userId != null) {
            LocalAuth localAuth = new LocalAuth();
            localAuth.setUserId(userId);
            localAuth.setUserName(username);
            localAuth.setPassword(password);

            try {
                //绑定账号
                LocalAuthExecution le = localAuthService.bindLocalAuthWithWechat(localAuth);
                LocalAuth localAuth1 = localAuthService.getLocalAuthByUserId(userId);

                if (le.getState() == LocalAuthStateEnum.SUCCESS.getState()) {
                    modelMap.put(SUCCESS, true);

                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, le.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入正确绑定信息");
        }
        return modelMap;
    }

    @PostMapping("/localAuthLogin")
    @ResponseBody
    private Map<String, Object> localAuthLogin(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        //获取是否需要验证码校验flag,输入密码错误过多要验证
        boolean needVerify = HttpServletRequestUtil.getBoolean(request, "needVerify");
        if (needVerify && !VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误");
            return modelMap;
        }
        //获取用户输入的账号
        String username = HttpServletRequestUtil.getString(request, USERNAME);
        //获取用户输入的密码
        String password = HttpServletRequestUtil.getString(request, PASSWORD);
        if (username != null && password != null) {
            try {
                LocalAuth localAuth = localAuthService.localAuthLogin(username, password);
                //如果通过用户名密码查到账号登陆成功
                if (localAuth != null) {
                    modelMap.put(SUCCESS, true);
                    //session中设置用户信息
                    request.getSession().setAttribute(USER, localAuth.getPersonInfo());
                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, "用户名或密码错误");
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.toString());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请输入正确登录信息");
        }
        return modelMap;

    }

    /**
     * 本地平台账号密码修改
     *
     * @param request
     * @return
     */
    @PostMapping("/updateLocalAuthPwd")
    @ResponseBody
    private Map<String, Object> updateLocalAuthPwd(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "验证码错误");
            return modelMap;
        }
        //获取用户输入的账号
        String username = HttpServletRequestUtil.getString(request, USERNAME);
        //获取用户输入的密码
        String password = HttpServletRequestUtil.getString(request, PASSWORD);
        //获取新密码
        String newPassword = HttpServletRequestUtil.getString(request, NEW_PASSWORD);
        //session中获取用户信息userId
        PersonInfo personInfo = (PersonInfo) request.getSession().getAttribute(USER);
        if (personInfo == null) {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请先登录在操作");
            return modelMap;
        }
        Integer userId = personInfo.getUserId();

        //判断条件
        if (username != null && password != null && userId != null && newPassword != null && !password.equals(newPassword)) {
            try {
                //修改账号密码
                LocalAuthExecution le = localAuthService.updateLocalAuthPwd(userId, username, password, newPassword);

                if (le.getState() == LocalAuthStateEnum.SUCCESS.getState()) {
                    //修改密码后清除session需要重新登录
                    request.getSession().setAttribute(USER, null);
                    modelMap.put(SUCCESS, true);

                } else {
                    modelMap.put(SUCCESS, false);
                    modelMap.put(ERR_MSG, le.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put(SUCCESS, false);
                modelMap.put(ERR_MSG, e.getMessage());
                return modelMap;
            }
        } else {
            modelMap.put(SUCCESS, false);
            modelMap.put(ERR_MSG, "请正确输入修改信息");
        }
        return modelMap;
    }

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/loginOut")
    @ResponseBody
    private Map<String, Object> loginOut(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        request.getSession().setAttribute(USER, null);
        modelMap.put(SUCCESS, true);
        return modelMap;
    }
}
