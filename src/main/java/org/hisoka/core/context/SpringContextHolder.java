package org.hisoka.core.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
@Component("springContextHolder")
public class SpringContextHolder implements ApplicationContextAware {

    public static volatile ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

}