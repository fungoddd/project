package com.fun.config;

import com.fun.util.cache.JedisPoolWriper;
import com.fun.util.cache.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;


/**
 * @Author: FunGod
 * @Date: 2018-12-11 18:15:51
 * @Desc: redis配置
 */
@Configuration
@EnableCaching//加入缓存
public class RedisConfiguration {
    @Value("${redis.hostname}")
    private String hostname;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.database}")
    private int database;

    @Value("${redis.pool.maxActive}")
    private int maxTotal;

    @Value("${redis.pool.maxIdle}")
    private int maxIdle;

    @Value("${redis.pool.maxWait}")
    private int maxWaitMills;

    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;

    @Autowired
    private JedisPoolConfig jedisPoolConfig;
    @Autowired
    private JedisPoolWriper jedisWritePool;
    @Autowired
    private JedisUtil jedisUtil;

    /**
     * 创建redis连接池设置
     *
     * @return
     */
    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //设置一个pool可以分配多少redis实例
        jedisPoolConfig.setMaxTotal(maxTotal);
        //连接池中最多可以空闲maxIdle个连接，设置20，表示即使没有数据库连接时依然可以保持20个空闲连接
        jedisPoolConfig.setMaxIdle(maxIdle);
        //最大等待时间：当没有可用连接时，连接池等待连接被归还的最大时间（一毫秒计数）,超过时间则抛出异常
        jedisPoolConfig.setMaxWaitMillis(maxWaitMills);
        //在获取连接的时候检查有效性
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);

        return jedisPoolConfig;
    }

    /**
     * 创建redis连接池并处理相关配置
     *
     * @return
     */
    @Bean(name = "jedisWritePool")
    public JedisPoolWriper createJedisPoolWriper() {
        JedisPoolWriper jedisPoolWriper = new JedisPoolWriper(jedisPoolConfig, hostname, port);
        return jedisPoolWriper;
    }

    /**
     * 创建redis工具类,封装好redis连接以进行相关操作
     * 默认是singleton的
     */
    @Bean(name = "jedisUtil")
    public JedisUtil createJedisUtil() {
        JedisUtil jedisUtil = new JedisUtil();
        jedisUtil.setJedisPool(jedisWritePool);
        return jedisUtil;
    }

    /**
     * redis的key操作
     */
    @Bean(name = "jedisKeys")
    public JedisUtil.Keys createJedisKeys() {
        JedisUtil.Keys jedisKeys = jedisUtil.new Keys();
        return jedisKeys;
    }

    @Bean(name = "jedisStrings")
    public JedisUtil.Strings createJedisStrings() {
        JedisUtil.Strings jedisStrings = jedisUtil.new Strings();
        return jedisStrings;
    }

    @Bean(name = "jedisLists")
    public JedisUtil.Lists createJedisLists() {
        JedisUtil.Lists jedisLists = jedisUtil.new Lists();
        return jedisLists;
    }

    @Bean(name = "jedisHash")
    public JedisUtil.Hash createJedisHash() {
        JedisUtil.Hash jedisHash = jedisUtil.new Hash();
        return jedisHash;
    }

    @Bean(name = "jedisSets")
    public JedisUtil.Sets createJedisSet() {
        JedisUtil.Sets jedisSets = jedisUtil.new Sets();
        return jedisSets;
    }
}
