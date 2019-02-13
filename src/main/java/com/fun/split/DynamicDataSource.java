package com.fun.split;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;




public class DynamicDataSource extends AbstractRoutingDataSource {
    //决定目标的dataSource,通过这个key决定是哪个目标
    @Override
    protected Object determineCurrentLookupKey() {
        System.out.println("TEST--------------------------当前使用的数据源:");
        //返回不同的key
        return DynamicDataSourceHolder.getDbType();
    }


}
