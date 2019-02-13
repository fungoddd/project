package com.fun.dto.Echart;

import lombok.Data;

import java.util.List;

/**
 * @Author: FunGod
 * @Date: 2018-12-17 22:07:50
 * @Desc: Echart内的series项
 */
@Data
public class EchartSeries {
    private String name;
    private String type="bar";
    private List<Integer> data;
}
