package org.hisoka.orm.redis.annotation;

import java.lang.annotation.*;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Documented
public @interface RedisJsonField {

    boolean serialize() default true;

}
