package com.fun.service;

/**
 * @Author: FunGod
 * @Date: 2018-12-09 14:28:10
 * @Desc: redis缓存操作接口
 */
public interface CacheService {
    /**
     * 通过前缀key删除对应key-value下的所有key_value
     * @param keyPrefix
     */
    void removeCache(String keyPrefix);
}
