package com.fun.util.request;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 10:35:41
 * @Desc: 请求处理工具类
 */

public class HttpServletRequestUtil {
    public static int getInt(HttpServletRequest request, String key) {
        //从request中提取出key转换成整型
        try {
            return Integer.decode(request.getParameter(key));
        } catch (Exception e) {
            //转换失败返回-1
            return -1;
        }
    }

    public static Long getLong(HttpServletRequest request, String key) {
        //从request中提取出key转换成长整型
        try {
            return Long.valueOf(request.getParameter(key));
        } catch (Exception e) {
            //转换失败返回-1L
            return -1L;
        }
    }

    public static Double getDouble(HttpServletRequest request, String key) {
        //从request中提取出key转换成double型
        try {
            return Double.valueOf(request.getParameter(key));
        } catch (Exception e) {
            //转换失败返回-1d
            return -1d;
        }
    }

    public static String getString(HttpServletRequest request, String key) {
        //从request中提取出key转换成String型
        try {
            String res = request.getParameter(key);
            //如果不为空去空格
            if (res != null) {
                res = res.trim();
            }
            //如果是空串则res=null
            if ("".equals(res)) {
                res = null;
            }
            return res;
        } catch (Exception e) {
            //转换失败返回-1d
            return null;
        }
    }

    public static Boolean getBoolean(HttpServletRequest request, String key) {
        //从request中提取出key转换成长整型
        try {
            return Boolean.valueOf(request.getParameter(key));
        } catch (Exception e) {
            //转换失败返回-false
            return false;
        }
    }
}
