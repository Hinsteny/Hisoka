package org.hisoka.core.session.context;

import org.hisoka.common.util.session.HttpSessionProxy;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class SessionContext {

    private static final ThreadLocal<HttpSessionProxy> sessionContextHolder = new ThreadLocal<HttpSessionProxy>();

    public static HttpSessionProxy getSessionContext() {
        return sessionContextHolder.get();
    }

    public static boolean isExist() {
        if (sessionContextHolder.get() == null) {
            return false;
        }

        if (sessionContextHolder.get().getId() == null) {
            return false;
        }

        return true;
    }

    public static void setSessionContext(HttpSessionProxy httpSessionProxy) {
        sessionContextHolder.set(httpSessionProxy);
    }

    public static void clean() {
        sessionContextHolder.remove();
    }
}
