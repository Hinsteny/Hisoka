package org.hisoka.common.util.cookie;

import org.hisoka.common.exception.SystemException;
import org.hisoka.common.util.secret.AESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public class CookieUtil {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(CookieUtil.class);

    private static String DEFAULT_ENCODE = "UTF-8";

    /**
     * 得到当前request请求的所有cookie
     *
     * @param request cookie数组
     * @return Cookie[]
     */
    public static Cookie[] getCookies(HttpServletRequest request) {
        return request == null ? null : request.getCookies();
    }

    /**
     * 根据cookie名字取得cookie
     *
     * @param request
     * @param name
     * @return cookie对象
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = getCookies(request);

        if (cookies != null && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                String cookName = cookie.getName();

                if (cookName != null && cookName.equals(name)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    /**
     * 根据cookie名字取得cookie的值
     *
     * @param request
     * @param name
     * @return cookie的值
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);

        try {
            if (cookie != null) {
                String value = cookie.getValue();
                value = URLDecoder.decode(value, DEFAULT_ENCODE);
                return value;
            }
        } catch (UnsupportedEncodingException e) {
            throw new SystemException("GetCookieValue error", e);
        }

        return null;
    }

    /**
     * 根据cookie名字取得cookie的值(解密后的值)
     *
     * @param request
     * @param name
     * @param aesKey
     * @return cookie的值
     */
    public static String getCookieValue(HttpServletRequest request, String name, String aesKey) {
        Cookie cookie = getCookie(request, name);
        try {
            if (cookie != null) {
                String value = cookie.getValue();
                value = AESUtil.decryptStr(value, aesKey);
                value = URLDecoder.decode(value, DEFAULT_ENCODE);
                return value;
            }
        } catch (UnsupportedEncodingException e) {
            throw new SystemException("GetCookieValue error", e);
        }

        return null;
    }

    /**
     * 创建加密cookie(默认使用AES加密)
     *
     * @param name
     * @param value
     * @param domain
     * @param path
     * @param secure
     * @param httpOnly
     * @param maxAge
     * @param aesKey
     * @return Cookie
     */
    public static Cookie createCookie(String name, String value, String domain, String path, boolean secure, boolean httpOnly, int maxAge, String aesKey) {
        try {
            value = URLEncoder.encode(value, DEFAULT_ENCODE);
            value = AESUtil.encryptStr(value, aesKey);
            Cookie cookie = new Cookie(name, value);
            cookie.setDomain(domain);
            cookie.setPath(path);
            cookie.setSecure(secure);
            cookie.setHttpOnly(httpOnly);
            cookie.setMaxAge(maxAge);
            return cookie;
        } catch (UnsupportedEncodingException e) {
            throw new SystemException("CreateCookie error", e);
        }
    }

    /**
     * 创建一个cookie
     *
     * @param name
     * @param value
     * @param domain
     * @param path
     * @param secure
     * @param httpOnly
     * @param maxAge
     * @return Cookie
     */
    public static Cookie createCookie(String name, String value, String domain, String path, boolean secure, boolean httpOnly, int maxAge) {
        try {
            value = URLEncoder.encode(value, DEFAULT_ENCODE);
            Cookie cookie = new Cookie(name, value);
            cookie.setDomain(domain);
            cookie.setPath(path);
            cookie.setSecure(secure);
            cookie.setHttpOnly(httpOnly);
            cookie.setMaxAge(maxAge);
            return cookie;
        } catch (UnsupportedEncodingException e) {
            throw new SystemException("CreateCookie error", e);
        }
    }

    /**
     * 将cookie写入客户端
     *
     * @param response
     * @param cookie   要写入的cookie
     */
    public static void writeCookie(HttpServletResponse response, Cookie cookie) {
        if (response != null) {
            response.addCookie(cookie);
        } else {
            log.warn("cookie is null");
        }
    }

    /**
     * 删除客户端cookie
     *
     * @param request
     * @param response
     * @param name     cookie名称
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name, String domain, String path) {
        Cookie cookie = getCookie(request, name);

        if (cookie != null) {
            removeCookie(response, cookie, domain, path);
        }
    }

    /**
     * 删除客户端cookie
     *
     * @param response
     * @param cookie   cookie对象
     */
    public static void removeCookie(HttpServletResponse response, Cookie cookie, String domain, String path) {
        if (cookie != null) {
            cookie.setMaxAge(0);
            cookie.setDomain(domain);
            cookie.setPath(path);
            response.addCookie(cookie);
        } else {
            log.warn("cookie is null");
        }
    }

}