package org.hisoka.orm.redis.callback;

import org.springframework.data.redis.core.SessionCallback;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public abstract class SessionCallBack<T> implements SessionCallback<T> {

    public abstract String getKey();
}
