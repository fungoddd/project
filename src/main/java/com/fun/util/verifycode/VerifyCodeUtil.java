package com.fun.util.verifycode;

import com.fun.util.request.HttpServletRequestUtil;
import com.google.code.kaptcha.Constants;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author: FunGod
 * @Date: 2018-12-07 10:37:46
 * @Desc: 验证码校验和生成二维码工具类
 */
public class VerifyCodeUtil {
    /**
     * 验证码校验
     *
     * @param request
     * @return
     */
    public static boolean checkVerifyCode(HttpServletRequest request) {

        //获取到生成的验证码的值
        String code = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);

        //当前用户输入验证码
        String curCode = HttpServletRequestUtil.getString(request, "verifyCode");

        //转为大写,如果验证码为空或者不一致返回false
        if (curCode == null || !curCode.toUpperCase().equals(code.toUpperCase())) {
            return false;
        }
        return true;
    }

    /**
     * 生成二维码的图片流:bit矩阵(二维数组)
     */
    public static BitMatrix generatorQRCodeStream(String content, HttpServletResponse response) {
        //给响应添加头部信息,告知浏览器返回的是图片流
        //告知不要缓存图片
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");
        //设置图片的文字编码和内边框间距
        Map<EncodeHintType, Object> hints = new HashMap<>();
        //UTF-8编码
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //内边框距0
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix;
        try {
            //入参:编码内容,编码类型,图片宽度,高度,设置的编码内容和内边距参数
            bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, 300, 300, hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        return bitMatrix;
    }
}
