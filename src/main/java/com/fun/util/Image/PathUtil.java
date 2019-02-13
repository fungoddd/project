package com.fun.util.Image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 10:38:44
 * @Desc: 图片路径处理工具类
 */
@Configuration//读取外部配置文件内容
public class PathUtil {
    //获取当前系统文件的分隔符
    private static String separator = System.getProperty("file.separator");

    private static String winPath;
    private static String linxuPath;
    private static String shopPath;

    @Value("${win.base.path}")
    public void setWinPath(String winPath) {
        PathUtil.winPath = winPath;
    }

    @Value("${linux.base.path}")
    public void setLinxuPath(String linxuPath) {
        PathUtil.linxuPath = linxuPath;
    }

    @Value("${shop.relevant.path}")
    public void setShopPath(String shopPath) {
        PathUtil.shopPath = shopPath;
    }

    //返回项目图片的根路径(图片所在根目录)
    public static String getImgBasePath() {
        //获取当前操作系统
        String os = System.getProperty("os.name");
        String basePath = "";
        //若当前是windows操作系统
        if (os.toLowerCase().startsWith("win")) {
            //图片上传后存储到此目录下
            basePath = winPath;
        } else {
            basePath = linxuPath;
        }
        //针对不同操作系统进行统一替换
        basePath = basePath.replace("/", separator);
        //返回图片的根路径
        return basePath;
    }

    //返回各个店铺下的图片的子路径
    public static String getShopImgPath(Integer shopId) {
        String imgPath = shopPath + shopId + "/";
        return imgPath.replace("/", separator);
    }
}
