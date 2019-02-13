package com.fun.split;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程安全管理数据源
 */
//保留determineCurrentLookupKey()所需要的key
public class DynamicDataSourceHolder {
    private static Logger logger = LoggerFactory.getLogger(DynamicDataSourceHolder.class);
    //保证线程安全
    private static ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static final List<Object> supportList = new ArrayList<>();

    public static final String DB_MASTER = "master";

    public static final String DB_SLAVE = "slave";

    /**
     * 获取线程的dbType
     *
     * @return
     */
    public static String getDbType() {
        String db = contextHolder.get();
        if (db == null) {
            db = DB_MASTER;
        }
        System.out.println("TEST------------当前数据源" + db);
        return db;
    }

    /**
     * 设置线程的dbType
     *
     * @param dbType
     */
    public static void setDbType(String dbType) {
        //添加日志信息,提示当前使用了什么数据源
        logger.debug("所用数据源为:" + dbType);
        contextHolder.set(dbType);
    }

    /**
     * 清理连接类型
     */
    public static void clearDbType() {
        contextHolder.remove();
    }

    /**
     * 获取数据源判断是否支持管理
     *
     * @param type
     * @return
     */
    public static boolean support(String type) {
        return supportList.contains(type);
    }

}
