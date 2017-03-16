package org.hisoka.orm.relative.interceptor;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.hisoka.common.exception.SystemException;
import org.mybatis.spring.SqlSessionFactoryBean;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/20
 * @copyright: 2016 All rights reserved.
 */
public class InterceptorUtil {

    private static SqlSessionFactoryBean sqlSessionFactoryBean;

    public static void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        if (InterceptorUtil.sqlSessionFactoryBean == null) {
            InterceptorUtil.sqlSessionFactoryBean = sqlSessionFactoryBean;
        }
    }

    public static void dynamicAddInterceptor(String typeClass) {
        try {
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
            Configuration configuration = sqlSessionFactory.getConfiguration();
            TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
            Interceptor interceptor = (Interceptor) resolveClass(typeClass, typeAliasRegistry).newInstance();
            configuration.addInterceptor(interceptor);
        } catch (Exception e) {
            throw new SystemException("DynamicAddInterceptor error", e);
        }
    }

    private static Class<?> resolveClass(String alias, TypeAliasRegistry typeAliasRegistry) {
        if (alias == null)
            return null;
        try {
            return resolveAlias(alias, typeAliasRegistry);
        } catch (Exception e) {
            throw new BuilderException("Error resolving class . Cause: " + e, e);
        }
    }

    private static Class<?> resolveAlias(String alias, TypeAliasRegistry typeAliasRegistry) {
        return typeAliasRegistry.resolveAlias(alias);
    }
}

