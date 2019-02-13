package com.fun.split;


import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.Properties;

/*
设置MyBatis 的拦截器(需要使用DataSource 路由就需要通过MyBatis 拦截器，拦截SQL 请求，根据SQL
 *       类型选择不同的数据源) DynamicDataSourceInterceptor 拦截器写好没用需要在 MyBatis 配置文件中配置方可使用
 *       <plugins> <plugin interceptor="DynamicDataSourceInterceptor拦截器类路径" />
 *       </plugins>
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class DynamicDataSourceInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(DynamicDataSourceInterceptor.class);
    /* 匹配写操作的正则规则 */
    private static final String UPDATE_REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";

    /**
     * 拦截mybatis读写操作,严格控制读写分离,主库写,从库读
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //判断当前是否是事务Transactional,如果是事务管理起来的会返回true
        boolean synchronizationActive = TransactionSynchronizationManager.isActualTransactionActive();
        System.out.println("TEST----------------是否是事务操作?" + synchronizationActive);
        //获取crud对象的操作参数
        Object[] objects = invocation.getArgs();
        //在头部,一个MappedStatement对象对应Mapper配置文件中的一个select/update/insert/delete节点，主要描述的是一条SQL语句。
        MappedStatement ms = (MappedStatement) objects[0];
        //默认选择主数据库
        String lookUpKey = DynamicDataSourceHolder.DB_MASTER;

        //如果非事务管理的情况
        if (synchronizationActive != true) {

            //读方法,如果sql是查询的话
            if (ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
                //主库进行了插入数据操作后，通常需要获取执行结果的自增主键，
                //selectKey为自增id查询主键(SELECT LAST_INSERT_ID())方法,使用主数据库master,写数据
                if (ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
                    lookUpKey = DynamicDataSourceHolder.DB_MASTER;
                } else { //ms.getBoundSql(objects[1])
                    BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
                    ///根据中国时区,将所有制表符,换行符回车替换成空格
                    String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
                    //判断这个sql语句是不是增删改,如果是用主库
                    if (sql.matches(UPDATE_REGEX)) {
                        lookUpKey = DynamicDataSourceHolder.DB_MASTER;
                    } else {//否则使用从库(读操作)一定要注意不能让从数据库执行了写操作。
                        lookUpKey = DynamicDataSourceHolder.DB_SLAVE;
                    }
                }
            }
        } else {//如果是事务管理的,一般都是主库进行的

            lookUpKey = DynamicDataSourceHolder.DB_MASTER;

        }
        System.out.println("TEST---------------使用了什么策略?SQL方法?");
        //打印日志使用了什么策略
        logger.debug("设置方法[{}] use [{}] Strategy,SqlCommandType[{}]...",
                ms.getId(), lookUpKey, ms.getSqlCommandType().name());
        // 保存连接类型的选择
        DynamicDataSourceHolder.setDbType(lookUpKey);
        //继续执行后续流程
        return invocation.proceed();
    }

    /*决定返回封装好的对象或者代理对象
     拦截的对象是Executor类型进行拦截,如果不是返回本体不进行拦截
     Executor有一系列crud的对象
     Executor,Plugin 是MyBatis 提供的类
     当拦截的对象是 Executor 这个类型，就进行拦截，就将 intercept 包装到 wrap 中去。如果不是就直接返回本体，不受拦截
     Executor 在MyBatis 中是用来支持一系列增删改查操作。意思就是检测是否有CRUD操作，有就拦截，没有就放过
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }


}
