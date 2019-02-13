package com.fun.util.Image;

import lombok.Data;

import java.io.InputStream;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 10:36:14
 * @Desc: 图片处理, 两种数据类型合并规范
 */

@Data
public class ImageHelper {

    private String imageName;
    private InputStream image;

    public ImageHelper(String imageName, InputStream image) {
        this.imageName = imageName;
        this.image = image;
    }
}
