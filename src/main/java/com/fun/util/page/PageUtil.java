package com.fun.util.page;



/**
 * @Author: FunGod
 * @Date: 2018-12-07 10:38:19
 * @Desc: 分页处理工具类,如果页数大于0,所查询的条为页数减1(如查询第1页,则从第0条开始查)第一页的第0条选取乘上每页数据条数
 */
public class PageUtil {
    public static int calculateRowIndex(int pageIndex, int pageSize) {
        return (pageIndex > 0) ? (pageIndex - 1) * pageSize : 0;

    }
}
