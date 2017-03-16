package org.hisoka.orm.redis.apply;

/**
 * @author Hinsteny
 * @Describtion redis动态数据源选择器
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class RedisSourceSwitcher {

    private static final ThreadLocal<String> redisContextHolder = new ThreadLocal<String>();

    public static void setRedisSourceTypeInContext(String redisSourceType) {
        redisContextHolder.set(redisSourceType);
    }

    public static String getRedisSourceType() {
        String redisSourceType = (String) redisContextHolder.get();
        return redisSourceType;
    }

    public static String getRedisSourceTypeFromContext() {
        String redisSourceType = (String) redisContextHolder.get();
        return redisSourceType;
    }

    public static void clearRedisSourceType() {
        redisContextHolder.remove();
    }
}

