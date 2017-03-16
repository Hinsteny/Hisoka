package org.hisoka.core.session.apply.impl;

import org.hisoka.common.util.session.HttpSessionProxy;
import org.hisoka.common.util.session.SessionUtil;
import org.hisoka.core.session.apply.SessionPersister;
import org.hisoka.core.session.config.SessionConfig;
import org.hisoka.core.session.config.SessionMetadata;
import org.hisoka.orm.redis.apply.DynamicRedisSource;
import org.hisoka.orm.redis.callback.RedisCallBack;
import org.hisoka.orm.redis.serialize.RedisSerializer;
import org.springframework.data.redis.connection.RedisConnection;

import java.util.concurrent.ConcurrentMap;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class RedisSessionPersister implements SessionPersister {

    // private static final String DEFAULT_SESSION_KEY = "Session:";

    private static final String DEFAULT_SESSION_KEY = "";

    private DynamicRedisSource dynamicRedisSource;

    public void setDynamicRedisSource(DynamicRedisSource dynamicRedisSource) {
        this.dynamicRedisSource = dynamicRedisSource;
    }

    @Override
    public HttpSessionProxy getSessionFromCache(final String key) {
        HttpSessionProxy httpSessionProxy = dynamicRedisSource.execute(new RedisCallBack<HttpSessionProxy>() {
            @Override
            public String getKey() {
                return DEFAULT_SESSION_KEY + key;
            }

            @Override
            public HttpSessionProxy doInRedis(RedisConnection connection, byte[] key) {
                byte[] bytes = connection.get(key);
                SessionMetadata sessionMetadata = RedisSerializer.deserialize(SessionMetadata.class, bytes, dynamicRedisSource.getValueSerializer());
                HttpSessionProxy httpSessionProxy = null;

                if (sessionMetadata != null) {
                    httpSessionProxy = getHttpSessionProxy(sessionMetadata);
                }

                return httpSessionProxy;
            }
        });

        return httpSessionProxy;
    }

    @Override
    public boolean addSessionToCache(final HttpSessionProxy httpSession, final SessionConfig sessionConfig) {
        if (httpSession == null) {
            return false;
        }

        final SessionMetadata sessionMetadata = getSessionMetadata(httpSession);

        boolean result = dynamicRedisSource.execute(new RedisCallBack<Boolean>() {
            @Override
            public String getKey() {
                return DEFAULT_SESSION_KEY + sessionMetadata.getSessionId();
            }

            @Override
            public Boolean doInRedis(RedisConnection connection, byte[] key) {
                if (getExpired(sessionMetadata)) {
                    connection.del(key);
                } else {
                    long currentTime = System.currentTimeMillis();
                    sessionMetadata.setLastAccessedTime(currentTime);
                    long timeout = sessionMetadata.getMaxInactiveInterval() / 1000;
                    connection.setEx(key, timeout, RedisSerializer.serialize(sessionMetadata, dynamicRedisSource.getValueSerializer()));
                }

                return true;
            }
        });

        return result;
    }

    private HttpSessionProxy getHttpSessionProxy(SessionMetadata sessionMetadata) {
        String sessionId = sessionMetadata.getSessionId();
        long creationTime = sessionMetadata.getCreationTime();
        int maxInactiveInterval = sessionMetadata.getMaxInactiveInterval();
        long lastAccessedTime = sessionMetadata.getLastAccessedTime();
        boolean expired = getExpired(sessionMetadata);
        boolean isNew = false;
        boolean isDirty = sessionMetadata.isChanged();
        ConcurrentMap<String, Object> data = sessionMetadata.getSessionMap();
        HttpSessionProxy httpSessionProxy = SessionUtil.createSession(sessionId, creationTime, maxInactiveInterval, lastAccessedTime, expired, isNew, isDirty,
                data);
        return httpSessionProxy;
    }

    private SessionMetadata getSessionMetadata(HttpSessionProxy httpSessionProxy) {
        SessionMetadata sessionMetadata = new SessionMetadata();
        sessionMetadata.setSessionId(httpSessionProxy.getId());
        sessionMetadata.setCreationTime(httpSessionProxy.getCreationTime());
        sessionMetadata.setLastAccessedTime(httpSessionProxy.getLastAccessedTime());
        sessionMetadata.setMaxInactiveInterval(httpSessionProxy.getMaxInactiveInterval());
        sessionMetadata.setSessionMap(httpSessionProxy.getData());
        sessionMetadata.setChanged(httpSessionProxy.isDirty());
        return sessionMetadata;
    }

    private boolean getExpired(SessionMetadata sessionMetadata) {
        return sessionMetadata.getLastAccessedTime() + sessionMetadata.getMaxInactiveInterval() <= System.currentTimeMillis();
    }

}
