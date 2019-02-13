package com.fun.util.Image;


import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 10:37:00
 * @Desc: 图片处理工具类
 */

public class ImageUtil {

    //保存在classpath下的图片路径     读取绝对路径
    private static String basePath = PathUtil.getImgBasePath();//Thread.currentThread().getContextClassLoader().getResource("").getPath();

    //时间格式与随机数，用来组成唯一的随机图片名
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final Random random = new Random();

    //验证是否为图片
    public static boolean isImg(InputStream in) throws IOException {
        try {
            BufferedImage bi = ImageIO.read(in);
            if (bi == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //缩略图处理用户传递过来的店铺图片和
    //CommonsMultipartFile用户传递的文件流
    //targetAddr用户保存图片的地址  所属文件夹的相对路径
    public static String generateThumbnail(ImageHelper thumbnail, String targetAddr) {
        //获取随机的文件名
        String realFileName = getRandomFileName();
        //获取输入流的文件扩展名
        String extensionName = getFileExtension(thumbnail.getImageName());
        //创建目录
        makeDirPath(targetAddr);
        //相对路径=图片的地址+随机文件名+扩展名
        String relativePath = targetAddr + realFileName + extensionName;
        //新的文件目标文件 绝对路径(图片的根路径+相对路径)
        File newFile = new File(PathUtil.getImgBasePath() + relativePath);

        try {
            Thumbnails.of(thumbnail.getImage()).size(300, 300)//添加水印,水印图片中位置,水印的路径,水印透明度
                    .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/tempImg/logo1.jpg")), 0.50f)
                    //压缩比
                    .outputQuality(0.9f).toFile(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return relativePath;
    }

    /**
     * 详情图处理
     *
     * @param thumbnail
     * @param targetAddr
     * @return
     */
    public static String generateNormalImg(ImageHelper thumbnail, String targetAddr) {
        // 获取不重复的随机名
        String realFileName = getRandomFileName();
        // 获取文件的扩展名如gif,png，jpg等
        String extension = getFileExtension(thumbnail.getImageName());
        // 如果目标路径不存在，则自动创建
        makeDirPath(targetAddr);
        // 获取文件存储的相对路径（带文件名）
        String relativeAddr = targetAddr + realFileName + extension;
        // 获取文件要保存到的目标路径
        File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
        // 调用Thumbnails生成带有水印的图片详情图
        try {
            Thumbnails.of(thumbnail.getImage()).size(375, 667)
                    .watermark(Positions.BOTTOM_RIGHT,//水印logo所在路径
                            ImageIO.read(new File(basePath + "/tempImg/logo1.jpg")), 0.20f)
                    .outputQuality(0.9f)
                    .toFile(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回图片的相对路径
        return relativeAddr;
    }

    /**
     * storePath是文件路径或者是目录路径
     * 如果storePath是文件删除该文件,
     * 如果是目录删除该目录下所有文件
     *
     * @param storePath
     */
    public static void deleteFileOrPath(String storePath) {
        //获取到完整路径
        File fileOrPath = new File(PathUtil.getImgBasePath() + storePath);
        //如果存在
        if (fileOrPath.exists()) {
            //如果是个目录
            if (fileOrPath.isDirectory()) {
                //将目录下的文件遍历删除掉
                File[] files = fileOrPath.listFiles();
                for (File file : files) {
                    file.delete();
                }
            }//最后将这个目录页删除
            fileOrPath.delete();
        }
    }

    /**
     * 创建目标路径上所涉及到的路径，如c:/funProject/image\xxx.jpg
     * 若其中的funProject,image的文件夹不存在，则创建出来
     *
     * @param targetAddr
     */
    private static void makeDirPath(String targetAddr) {
        //目标图片应该存储的绝对路径
        String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
        File dirPath = new File(realFileParentPath);
        if (!dirPath.exists()) {
            //若文件夹不存在，则递归的创建出来
            dirPath.mkdirs();
        }
    }

    /**
     * 获取输入流的文件扩展名
     *
     * @param fileName
     * @return
     */
    private static String getFileExtension(String fileName) {
        //返回文件扩展名
        return fileName = fileName.substring(fileName.lastIndexOf("."));
    }


    /**
     * 生成随机文件名，当前年月日小时分钟秒+五位随机数
     *
     * @return
     */
    private static String getRandomFileName() {
        //随机的五位数 0-89999之间取值 10000-99999
        int randomNum = random.nextInt(89999) + 10000;
        String nowTimeStr = simpleDateFormat.format(new Date());

        return nowTimeStr + randomNum;
    }

}

