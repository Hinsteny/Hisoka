package org.hisoka.orm.redis.template;

import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * @author Hinsteny
 * @Describtion Redis的操作模板代理
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class RedisTemplateProxy {

    private String redisSourceKey;

    private List<RedisTemplate<Serializable, Serializable>> redisTemplateList;

    private boolean isDefault;

    public String getRedisSourceKey() {
        return redisSourceKey;
    }

    public void setRedisSourceKey(String redisSourceKey) {
        this.redisSourceKey = redisSourceKey;
    }

    public List<RedisTemplate<Serializable, Serializable>> getRedisTemplateList() {
        return redisTemplateList;
    }

    public void setRedisTemplateList(List<RedisTemplate<Serializable, Serializable>> redisTemplateList) {
        this.redisTemplateList = redisTemplateList;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "RedisTemplateProxy [redisSourceKey=" + redisSourceKey + ", redisTemplateList=" + redisTemplateList + ", isDefault=" + isDefault + "]";
    }

}

