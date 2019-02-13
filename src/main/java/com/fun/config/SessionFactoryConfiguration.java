package com.fun.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;


import java.io.IOException;

/**
 * 配置SqlSessionFactory对象
 */
@Configuration
public class SessionFactoryConfiguration {

    //@Autowired
    //private DataSource dataSource;
    //注入启用懒加载的数据库连接池
    @Autowired
    private LazyConnectionDataSourceProxy lazyConnectionDataSourceProxy;


    //mybatis配置文件路径
    private static String mybatis;
    //动态配置必须用set获取
    @Value("${mybatis-config}")
    public void setMybatis(String mybatis) {
        SessionFactoryConfiguration.mybatis = mybatis;
    }

    //mybatis mapper文件路径
    private static String mapperPath;
    //动态配置必须用set获取
    @Value("${mapper-path}")
    public void setMapperPath(String mapperPath) {
        SessionFactoryConfiguration.mapperPath = mapperPath;
    }

    //实体类包名
    @Value("${entity-path}")
    private String entity;

    /**
     * 创建sqlSessionFactoryBean实例并设置config 设置mapper映射路径,设置dataSource数据源
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean createSqlSessionFactoryBean() throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        //设置mybatis外部配置文件扫描路径
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(mybatis));
        //添加mapper映射文件扫描路径
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver =
                new PathMatchingResourcePatternResolver();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + mapperPath;
        sqlSessionFactoryBean.setMapperLocations(pathMatchingResourcePatternResolver.getResources(packageSearchPath));
        //设置dataSource
        //sqlSessionFactoryBean.setDataSource(dataSource);
        //注入启用懒加载的数据源
        sqlSessionFactoryBean.setDataSource(lazyConnectionDataSourceProxy);
        //设置实体类扫描路径
        sqlSessionFactoryBean.setTypeAliasesPackage(entity);

        return sqlSessionFactoryBean;
    }
}
