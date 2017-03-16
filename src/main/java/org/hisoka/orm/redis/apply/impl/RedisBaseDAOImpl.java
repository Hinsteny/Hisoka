package org.hisoka.orm.redis.apply.impl;

import org.hisoka.orm.redis.apply.DynamicRedisSource;
import org.hisoka.orm.redis.apply.RedisBaseDAO;
import org.hisoka.orm.redis.serialize.RedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class RedisBaseDAOImpl implements RedisBaseDAO{

    @Autowired(required = false)
    protected DynamicRedisSource redisTemplate;

    @Override
    public byte[] serialize(Object obj) {
        return RedisSerializer.serialize(obj);
    }

    @Override
    public <T> T deserialize(Class<T> type, byte[] bytes) {
        return RedisSerializer.deserialize(type, bytes);
    }

    @Override
    public <T> List<T> deserializeArray(Class<T> type, byte[] bytes) {
        return RedisSerializer.deserializeArray(type, bytes);
    }
}
