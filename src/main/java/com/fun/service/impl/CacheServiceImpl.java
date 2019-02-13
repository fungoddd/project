package com.fun.service.impl;

import com.fun.service.CacheService;
import com.fun.util.cache.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @Author: FunGod
 * @Date: 2018-12-09 14:30:00
 * @Desc: redis清除缓存业务层实现
 */
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private JedisUtil.Keys jedisKeys;

    /**
     * 删除redis缓存中指定前缀key对应的键值对value删除
     *
     * @param keyPrefix
     */
    @Override
    public void removeCache(String keyPrefix) {
        //定义一个set集合(不可重复),元素为redis中的keys类型的key
        Set<String> keySet = jedisKeys.keys(keyPrefix + "*");
        //遍历删除指定前缀的key下的key_value
        for (String key : keySet) {
            jedisKeys.del(key);
        }
    }
}
