package org.hisoka.core.session.apply;

import org.hisoka.common.util.session.HttpSessionProxy;
import org.hisoka.core.session.config.SessionConfig;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public interface SessionPersister {

    HttpSessionProxy getSessionFromCache(final String key);

    boolean addSessionToCache(final HttpSessionProxy httpSession, final SessionConfig sessionConfig);
}
