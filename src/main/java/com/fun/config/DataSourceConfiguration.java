package com.fun.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.fun.split.DynamicDataSource;
import com.fun.split.DynamicDataSourceHolder;
import com.fun.util.DES.DESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: FunGod
 * @Date: 2018-12-11 18:07:44
 * @Desc: 配置druid数据源
 */

//注入到IOC容器
@Configuration
public class DataSourceConfiguration extends AbstractDataSource {

    //主数据源配置连接信息
    @Value("${jdbc.master.driver}")
    private String masterjdbcDriver;
    @Value("${jdbc.master.url}")
    private String masterjdbcUrl;
    @Value("${jdbc.master.username}")
    private String masterjdbcUsername;
    @Value("${jdbc.master.password}")
    private String masterjdbcPassword;

    //从数据源配置连接信息
    @Value("${jdbc.slave.driver}")
    private String slavejdbcDriver;
    @Value("${jdbc.slave.url}")
    private String slavejdbcUrl;
    @Value("${jdbc.slave.username}")
    private String slavejdbcUsername;
    @Value("${jdbc.slave.password}")
    private String slavejdbcPassword;


    /**
     * 主数据源(写操作)
     *
     * @return
     * @throws SQLException
     */
    /*destroy-method="close"的作用是当数据库连接不使用的时候,就把该连接重新放到数据池中,方便下次使用调用.
     * BasicDataSource提供了close()方法关闭数据源，所以必须设定destroy-method=”close”属性，
     * 以便Spring容器关闭时，数据源能够正常关闭；销毁方法调用close(),是将连接关闭，并不是真正的把资源销毁。*/
    @Bean(name = "master", destroyMethod = "close")
    @Primary//优先于其他实例注入,先注入主数据源,一般用于多数据源的情况下
    public DataSource masterDataSource() throws SQLException {

        DruidDataSource druidDataSource = new DruidDataSource();
        //abstractDataSource.createDataSource();
        //跟配置文件一样设置信息
        //驱动
        druidDataSource.setDriverClassName(masterjdbcDriver);
        //数据库连接url
        druidDataSource.setUrl(masterjdbcUrl);
        //设置用户名
        druidDataSource.setUsername(DESUtil.getDecryptString(masterjdbcUsername));
        //密码
        druidDataSource.setPassword(DESUtil.getDecryptString(masterjdbcPassword));

        return druidDataSource;
    }

    /**
     * 从数据源(读操作)
     *
     * @return
     * @throws SQLException
     */
    @Bean(name = "slave", destroyMethod = "close")
    public DataSource slaveDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        //abstractDataSource.createDataSource();

        //跟配置文件一样设置信息
        //驱动
        druidDataSource.setDriverClassName(slavejdbcDriver);
        //数据库连接url
        druidDataSource.setUrl(slavejdbcUrl);
        //设置用户名
        druidDataSource.setUsername(DESUtil.getDecryptString(slavejdbcUsername));
        //密码
        druidDataSource.setPassword(DESUtil.getDecryptString(slavejdbcPassword));


        return druidDataSource;
    }

    /**
     * 配置动态数据源,targetDataSources是路由数据源所对应的名称
     *
     * @return
     * @throws SQLException
     */
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource creteDynamicDataSource() throws SQLException {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource());
        targetDataSources.put("slave", slaveDataSource());
        dynamicDataSource.setTargetDataSources(targetDataSources);
        //判断是否支持该数据源,把key对应的value数据源对象添加到list遍历
        DynamicDataSourceHolder.supportList.addAll(targetDataSources.keySet());
        return dynamicDataSource;
    }

    /**
     * mybatis生成sql语句才执行使用哪个数据库,启用懒加载
     *
     * @return
     * @throws SQLException
     */
    @Bean(name = "dataSource")
    public LazyConnectionDataSourceProxy creteLazyConnectionDataSourceProxy() throws SQLException {
        LazyConnectionDataSourceProxy dataSourceProxy = new LazyConnectionDataSourceProxy();
        dataSourceProxy.setTargetDataSource(creteDynamicDataSource());
        return dataSourceProxy;
    }





    /*@Value("${jdbc.driver}")
    private String jdbcDriver;
    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.username}")
    private String jdbcUsername;
    @Value("${jdbc.password}")
    private String jdbcPassword;*/

    /**
     * 生成与spring-dao.xml对应的bean datasource
     */

   /* @Bean(name = "dataSource")
    public DataSource createDataSource() throws SQLException {

        //生成dataSource实例
        DruidDataSource druidDataSource = new DruidDataSource();

        //跟配置文件一样设置信息
        //驱动
        druidDataSource.setDriverClassName(jdbcDriver);
        //数据库连接url
        druidDataSource.setUrl(jdbcUrl);
        //设置用户名
        druidDataSource.setUsername(DESUtil.getDecryptString(jdbcUsername));
        //密码
        druidDataSource.setPassword(DESUtil.getDecryptString(jdbcPassword));
        //相关配置信息
        druidDataSource.setMaxActive(30);
        druidDataSource.setMinIdle(5);
        druidDataSource.setInitialSize(5);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery("SELECT 'X'");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        // 打开PSCache，并且指定每个连接上PSCache的大小
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(30);
        druidDataSource.setFilters("stat");

        return druidDataSource;
    }*/

}
