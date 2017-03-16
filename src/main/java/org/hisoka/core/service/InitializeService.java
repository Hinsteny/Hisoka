package org.hisoka.core.service;

import org.hisoka.core.mq.apply.MqMessageSenderSwitcher;
import org.hisoka.orm.redis.apply.DynamicRedisSourceManager;
import org.hisoka.orm.relative.apply.DataSourceSwitcher;
import org.hisoka.orm.relative.apply.DynamicDataSourceManager;
import org.hisoka.rpc.dubbo.apply.DubboClientSwitcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
@Service("seInitializeService")
public class InitializeService implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired(required = false)
    private DynamicDataSourceManager dynamicDataSourceManager;

    @Autowired(required = false)
    private DynamicRedisSourceManager dynamicRedisSourceManager;

    @Autowired(required = false)
    private StartupCallback startupCallback;

    private volatile boolean initialFlag;

    @Override
    public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null && !initialFlag) {
            initialFlag = true;
            // 系统动态source初始化
            initDynamicSource();
            // 系统启动阶段的业务处理扩展
            initStartService();
            // 清理动态源
            clearDynamicSources();
        }
    }

    /**
     * 清理动态源
     */
    public static void clearDynamicSources() {
        DataSourceSwitcher.clearDataSourceType();
//        HbaseSourceSwitcher.clearHbaseSourceType();
//        MongoSourceSwitcher.clearMongoSourceType();
//        RedisSourceSwitcher.clearRedisSourceType();
//        MemcacheSourceSwitcher.clearMemcacheSourceType();
        DubboClientSwitcher.clearDubboClientType();
        MqMessageSenderSwitcher.clearMqMessageSenderContextType();
    }

    /**
     * 初始化系统数据源，包括数据库，缓存
     *
     * @param
     */
    private void initDynamicSource() {
        if (dynamicDataSourceManager != null) {
            dynamicDataSourceManager.initCreateDataSource();
        }

        if (dynamicRedisSourceManager != null) {
            dynamicRedisSourceManager.initCreateRedisSource();
        }

    }

    /**
     * 初始化系统启动阶段指行的业务扩展
     *
     * @param
     */
    private void initStartService() {
        if (startupCallback != null) {
            startupCallback.businessHandle();
        }
    }
}
