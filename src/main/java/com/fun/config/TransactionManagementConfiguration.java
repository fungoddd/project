package com.fun.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;


/**
 * @Author: FunGod
 * @Date: 2018-12-11 18:08:29
 * @Desc: 开启Spring-service的事务管理器
 * 实现TransactionManagementConfigurer以开启annotation-driven基于注解的声明式事务
 */
@Configuration
//开启事务注解支持
@EnableTransactionManagement
public class TransactionManagementConfiguration implements TransactionManagementConfigurer {

    /**
     * 注入DataSourceConfiguration中的dataSource,通过createDataSource()获取
     */
    //@Autowired
    //private DataSource dataSource;
    //注入启用懒加载的数据库连接池
    @Autowired
    private LazyConnectionDataSourceProxy lazyConnectionDataSourceProxy;

    /**
     * 事务注解需要返回PlatformTransactionManager的实现
     *
     * @return
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        //return new DataSourceTransactionManager(dataSource);
        return new DataSourceTransactionManager(lazyConnectionDataSourceProxy);
    }
}
