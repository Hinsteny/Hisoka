package org.hisoka.common.util.session;

import org.hisoka.common.util.cookie.CookieUtil;
import org.hisoka.common.util.other.UUIDUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class SessionUtil {

    /**
     * 从cookie中获取sessionId
     *
     * @param httpServletRequest
     * @return
     */
    public static String getSessionIdFromCookie(HttpServletRequest httpServletRequest, String name) {
        return CookieUtil.getCookieValue(httpServletRequest, name);
    }

    /**
     * 生成sessionId
     *
     * @return
     */
    public static String generateSessionId() {
        return UUIDUtil.getUUID().toUpperCase();
    }

    /**
     * 创建一个新的Session
     *
     * @param httpServletResponse
     * @param sessionName
     * @param domain
     * @param path
     * @param secure
     * @param httpOnly
     * @param maxAge
     * @return
     */
    public static HttpSessionProxy createSession(HttpServletResponse httpServletResponse, String sessionName, String domain, String path, boolean secure,
                                                 boolean httpOnly, int maxAge, int maxInactiveInterval) {
        String sessionId = generateSessionId();
        HttpSessionProxy httpSessionProxy = new HttpSessionProxy();
        httpSessionProxy.id = sessionId;
        httpSessionProxy.creationTime = System.currentTimeMillis();
        httpSessionProxy.lastAccessedTime = System.currentTimeMillis();
        httpSessionProxy.maxInactiveInterval = maxInactiveInterval;
        httpSessionProxy.isNew = true;
        Cookie cookie = CookieUtil.createCookie(sessionName, sessionId, domain, path, secure, httpOnly, maxAge);
        CookieUtil.writeCookie(httpServletResponse, cookie);
        return httpSessionProxy;
    }

    /**
     * 创建一个已有sessionid的Session
     *
     * @param sessionId
     * @param maxInactiveInterval
     * @return
     */
    public static HttpSessionProxy createSession(String sessionId, int maxInactiveInterval) {
        HttpSessionProxy httpSessionProxy = new HttpSessionProxy();
        httpSessionProxy.id = sessionId;
        httpSessionProxy.creationTime = System.currentTimeMillis();
        httpSessionProxy.lastAccessedTime = System.currentTimeMillis();
        httpSessionProxy.maxInactiveInterval = maxInactiveInterval;
        httpSessionProxy.isNew = true;
        return httpSessionProxy;
    }

    /**
     * 创建一个已有sessionid的Session
     *
     * @param sessionId
     * @param maxInactiveInterval
     * @return
     */
    public static HttpSessionProxy createSession(String sessionId, long creationTime, int maxInactiveInterval, long lastAccessedTime, boolean expired,
                                                 boolean isNew, boolean isDirty, ConcurrentMap<String, Object> data) {
        HttpSessionProxy httpSessionProxy = new HttpSessionProxy();
        httpSessionProxy.id = sessionId;
        httpSessionProxy.creationTime = creationTime;
        httpSessionProxy.maxInactiveInterval = maxInactiveInterval;
        httpSessionProxy.lastAccessedTime = lastAccessedTime;
        httpSessionProxy.expired = expired;
        httpSessionProxy.isNew = isNew;
        httpSessionProxy.isDirty = isDirty;
        httpSessionProxy.data = data;
        return httpSessionProxy;
    }

    /**
     * 修改session是否最新标志
     *
     * @param httpSessionProxy
     * @param isNew
     * @return
     */
    public static boolean modifySessionIsNew(HttpSessionProxy httpSessionProxy, boolean isNew) {
        httpSessionProxy.isNew = isNew;
        return true;
    }

    /**
     * 修改session是否无效
     *
     * @param httpSessionProxy
     * @param expired
     * @return
     */
    public static boolean modifySessioExpired(HttpSessionProxy httpSessionProxy, boolean expired) {
        httpSessionProxy.expired = expired;
        return true;
    }

    /**
     * 删除session
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param sessionName
     * @return
     */
    public static boolean deleteSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String sessionName, String domain,
                                        String path) {
        CookieUtil.removeCookie(httpServletRequest, httpServletResponse, sessionName, domain, path);
        return true;
    }
}
