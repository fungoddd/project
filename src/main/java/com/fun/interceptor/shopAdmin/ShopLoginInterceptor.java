package com.fun.interceptor.shopAdmin;

import com.fun.entity.PersonInfo;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static com.fun.constant.ControllerConst.USER;

/**
 * @Author: FunGod
 * @Date: 2018-12-10 01:33:06
 * @Desc: 店家登录验证拦截器
 */
public class ShopLoginInterceptor extends HandlerInterceptorAdapter {
    /**
     * 用户操作之前(Controller执行之前),改写preHandle里的逻辑,进行拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override                                                                          //需要拦截的
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //session中取出用户信息
      Object userObject = request.getSession().getAttribute(USER);

        if (userObject != null) {
            //如果用户信息不为空将用户信息转为实体类
            PersonInfo user = (PersonInfo) userObject;
            //验证保证用户不为空userId不为空且状态为1可用
            if (user != null && user.getUserId() != null && user.getUserId() > 0 && user.getEnableStatus() == 1) {
                //通过验证返回true,拦截器返回true用户可用继续正常执行操作
                return true;
            }
        }
        //如果不满足验证,跳转到商家管理的账号登录界面
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<script>");
        out.println("window.open ('" + request.getContextPath() + "/localAuth/login?userType=2','_self')");
        out.println("</script>");
        out.println("</html>");
        //停止执行controller的方法
        return false;
    }
}
