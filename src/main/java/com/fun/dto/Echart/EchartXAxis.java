package com.fun.dto.Echart;


import java.util.HashSet;

/**
 * @Author: FunGod
 * @Date: 2018-12-17 22:04:51
 * @Desc: Echart内的xAxis项
 */
public class EchartXAxis {
    private String type = "category";
    //去重
    private HashSet<String> data;

    public String getType() {
        return type;
    }

    public HashSet<String> getData() {
        return data;
    }

    public void setData(HashSet<String> data) {
        this.data = data;
    }
}
