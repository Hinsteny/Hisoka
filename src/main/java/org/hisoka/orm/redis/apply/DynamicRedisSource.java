package org.hisoka.orm.redis.apply;

import org.hisoka.common.exception.SystemException;
import org.hisoka.common.util.other.ConsistenHashUtil;
import org.hisoka.common.util.string.StringUtil;
import org.hisoka.orm.redis.callback.RedisCallBack;
import org.hisoka.orm.redis.callback.SessionCallBack;
import org.hisoka.orm.redis.template.RedisTemplateProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class DynamicRedisSource {
    public static Map<String, ConsistenHashUtil<RedisTemplate<Serializable, Serializable>>> consistenHashUtilMap;

    private RedisTemplateProxy defaultTargetRedisSource;

    private Map<String, RedisTemplateProxy> targetRedisSources;

    private RedisSerializer<String> stringSerializer;

    private RedisSerializer<Object> valueSerializer;

    public RedisTemplateProxy getDefaultTargetRedisSource() {
        return defaultTargetRedisSource;
    }

    public void setDefaultTargetRedisSource(RedisTemplateProxy defaultTargetRedisSource) {
        this.defaultTargetRedisSource = defaultTargetRedisSource;
    }

    public Map<String, RedisTemplateProxy> getTargetRedisSources() {
        return targetRedisSources;
    }

    public void setTargetRedisSources(Map<String, RedisTemplateProxy> targetRedisSources) {
        this.targetRedisSources = targetRedisSources;
    }

    public RedisSerializer<String> getStringSerializer() {
        return stringSerializer;
    }

    public void setStringSerializer(RedisSerializer<String> stringSerializer) {
        this.stringSerializer = stringSerializer;
    }

    public RedisSerializer<Object> getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(RedisSerializer<Object> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public <T> T execute(RedisCallBack<T> action) {
        String key = action.getKey();
        RedisTemplate<Serializable, Serializable> redisTemplate = getRedisTemplate(key);

        if (redisTemplate == null) {
            redisTemplate = getRedisTemplate(key, defaultTargetRedisSource);
        }

        if (redisTemplate == null) {
            throw new SystemException("Can not get a redisTemplate!");
        }

        return redisTemplate.execute(action);
    }

    public <T> T execute(RedisCallBack<T> action, boolean exposeConnection) {
        String key = action.getKey();
        RedisTemplate<Serializable, Serializable> redisTemplate = getRedisTemplate(key);

        if (redisTemplate == null) {
            redisTemplate = getRedisTemplate(key, defaultTargetRedisSource);
        }

        if (redisTemplate == null) {
            throw new SystemException("Can not get a redisTemplate!");
        }

        return redisTemplate.execute(action, exposeConnection);
    }

    public <T> T execute(RedisCallBack<T> action, boolean exposeConnection, boolean pipeline) {
        String key = action.getKey();
        RedisTemplate<Serializable, Serializable> redisTemplate = getRedisTemplate(key);

        if (redisTemplate == null) {
            redisTemplate = getRedisTemplate(key, defaultTargetRedisSource);
        }

        if (redisTemplate == null) {
            throw new SystemException("Can not get a redisTemplate!");
        }

        return redisTemplate.execute(action, exposeConnection, pipeline);
    }

    public <T> T execute(SessionCallBack<T> action) {
        String key = action.getKey();
        RedisTemplate<Serializable, Serializable> redisTemplate = getRedisTemplate(key);

        if (redisTemplate == null) {
            redisTemplate = getRedisTemplate(key, defaultTargetRedisSource);
        }

        if (redisTemplate == null) {
            throw new SystemException("Can not get a redisTemplate!");
        }

        return redisTemplate.execute(action);
    }

    public void afterPropertiesSet() {
        Set<Map.Entry<String, RedisTemplateProxy>> set = targetRedisSources.entrySet();

        for (Map.Entry<String, RedisTemplateProxy> entry : set) {
            String key = entry.getKey();
            RedisTemplateProxy redisTemplateProxy = entry.getValue();
            List<RedisTemplate<Serializable, Serializable>> redisTemplateList = redisTemplateProxy.getRedisTemplateList();

            if (redisTemplateList != null && !redisTemplateList.isEmpty()) {
                if (DynamicRedisSource.consistenHashUtilMap == null) {
                    DynamicRedisSource.consistenHashUtilMap = new HashMap<String, ConsistenHashUtil<RedisTemplate<Serializable, Serializable>>>();
                }

                DynamicRedisSource.consistenHashUtilMap.put(key, new ConsistenHashUtil<RedisTemplate<Serializable, Serializable>>(redisTemplateList));

                for (RedisTemplate<Serializable, Serializable> redisTemplate : redisTemplateList) {
                    redisTemplate.afterPropertiesSet();
                }
            }
        }
    }

    private RedisTemplate<Serializable, Serializable> getRedisTemplate(String key) {
        String redisSourceType = RedisSourceSwitcher.getRedisSourceType();

        if (StringUtil.isNotBlank(redisSourceType)) {
            RedisTemplateProxy redisTemplateProxy = targetRedisSources.get(redisSourceType);
            return getRedisTemplate(key, redisTemplateProxy);
        } else {
            return null;
        }
    }

    private RedisTemplate<Serializable, Serializable> getRedisTemplate(String key, RedisTemplateProxy redisTemplateProxy) {
        if (redisTemplateProxy == null) {
            return null;
        }

        List<RedisTemplate<Serializable, Serializable>> redisTemplateList = redisTemplateProxy.getRedisTemplateList();

        if (redisTemplateList == null || redisTemplateList.isEmpty()) {
            return null;
        }

        String redisSourceType = redisTemplateProxy.getRedisSourceKey();
        ConsistenHashUtil<RedisTemplate<Serializable, Serializable>> consistenHashUtil = DynamicRedisSource.consistenHashUtilMap.get(redisSourceType);

        if (consistenHashUtil != null) {
            RedisTemplate<Serializable, Serializable> redisTemplate = consistenHashUtil.get(key);
            return redisTemplate;
        } else {
            return null;
        }
    }

}

