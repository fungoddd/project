package com.fun.util.converters;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: FunGod
 * @Date: 2018-12-07 11:56:07
 * @Desc: 自定义数据类型转换器
 */
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {

        int len = source.length();
        SimpleDateFormat sdf = null;
        switch (len) {
            case 10:
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    return sdf.parse(source);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 8:
                sdf = new SimpleDateFormat("HH:mm:ss");
                try {
                    return sdf.parse(source);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 19:
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    return sdf.parse(source);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
        return null;
    }

    // 1， 必须要实现 Converter 这个 接口
    // 2, 添加未实现的方法 convert

}
