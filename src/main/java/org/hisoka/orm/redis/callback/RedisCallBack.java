package org.hisoka.orm.redis.callback;

import org.hisoka.orm.redis.serialize.RedisSerializer;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public abstract class RedisCallBack<T> implements RedisCallback<T> {

    public abstract String getKey();

    public abstract T doInRedis(RedisConnection connection, byte[] key);

    @Override
    public T doInRedis(RedisConnection connection) throws DataAccessException {
        String key = getKey();
        return doInRedis(connection, RedisSerializer.serialize(key));
    }
}
