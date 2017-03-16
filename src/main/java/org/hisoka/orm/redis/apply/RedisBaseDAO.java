package org.hisoka.orm.redis.apply;

import java.util.List;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public interface RedisBaseDAO {

    /**
     * 序列化對象
     *
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化对象
     * @param type
     * @param bytes
     * @return
     */
    <T> T deserialize(Class<T> type, byte[] bytes);

    /**
     * 反序列化对象转化为列表
     * @param type
     * @param bytes
     * @return
     */
    <T> List<T> deserializeArray(Class<T> type, byte[] bytes);
}
