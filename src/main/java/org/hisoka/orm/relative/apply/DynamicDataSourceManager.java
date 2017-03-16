package org.hisoka.orm.relative.apply;

import org.hisoka.core.context.SpringContextHolder;
import org.hisoka.orm.relative.client.DataSourceProxy;
import org.hisoka.orm.relative.ddl.DdlDb;
import org.hisoka.orm.relative.ddl.interceptor.DdlInterceptor;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/21
 * @copyright: 2016 All rights reserved.
 */
public class DynamicDataSourceManager {

    private DynamicDataSource dynamicDataSource;

    private List<DataSourceProxy> dataSourceProxyList;

    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    public void setDataSourceProxyList(List<DataSourceProxy> dataSourceProxyList) {
        this.dataSourceProxyList = dataSourceProxyList;
    }

    /**
     * 初始化数据库数据源
     *
     */
    public void initCreateDataSource() {
        // 把数据源bean注册到容器中
        registerDataSource();
    }

    private void registerDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        Map<String, DdlDb> dbDataSourceMap = new HashMap<String, DdlDb>();
        Map<String, String> dataSourceDbMap = new HashMap<String, String>();
        DataSource defaultTargetDataSource = null;
        String defaultTargetDataSourceKey = null;
        List<DataSourceProxy> dataSourceProxyList = new ArrayList<DataSourceProxy>();

        if (this.dataSourceProxyList == null || this.dataSourceProxyList.isEmpty()) {
            Map<String, DataSourceProxy> dataSourceProxyMap = SpringContextHolder.applicationContext.getBeansOfType(DataSourceProxy.class);

            if (dataSourceProxyMap != null && !dataSourceProxyMap.isEmpty()) {
                for (Map.Entry<String, DataSourceProxy> en : dataSourceProxyMap.entrySet()) {
                    dataSourceProxyList.add(en.getValue());
                }
            }
        } else {
            dataSourceProxyList = this.dataSourceProxyList;
        }

        // 将默认数据源放入 targetDataSources和单元测试数据源加入到 map中
        for (DataSourceProxy dataSourceProxy : dataSourceProxyList) {
            String dataSourceKey = dataSourceProxy.getDataSourceKey();
            DataSource dataSource = dataSourceProxy.getDataSource();
            String dbName = dataSourceProxy.getDbName();
            boolean isDefault = dataSourceProxy.getIsDefault();
            Map<String, DataSource> slaveDataSourceMap = dataSourceProxy.getSlaveDataSourceMap();

            if (isDefault) {
                defaultTargetDataSource = dataSource;
                defaultTargetDataSourceKey = dataSourceKey;
            }

            if (slaveDataSourceMap != null && !slaveDataSourceMap.isEmpty()) {
                List<String> slaveDataSourceKeyList = new ArrayList<String>();

                for (Map.Entry<String, DataSource> sen : slaveDataSourceMap.entrySet()) {
                    String slaveDataSourceKey = sen.getKey();
                    DataSource slaveDataSource = sen.getValue();
                    slaveDataSourceKeyList.add(slaveDataSourceKey);
                    targetDataSources.put(slaveDataSourceKey, slaveDataSource);
                    dataSourceDbMap.put(slaveDataSourceKey, dbName);
                }

                dbDataSourceMap.put(dbName, new DdlDb(dbName, dataSourceKey, slaveDataSourceKeyList));
            } else {
                dbDataSourceMap.put(dbName, new DdlDb(dbName, dataSourceKey, null));
            }

            dataSourceDbMap.put(dataSourceKey, dbName);
            targetDataSources.put(dataSourceKey, dataSource);
        }

        dynamicDataSource.setDbDataSourceMap(dbDataSourceMap);
        dynamicDataSource.setDataSourceDbMap(dataSourceDbMap);
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDataSourceMap(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(defaultTargetDataSource);
        dynamicDataSource.setDefaultTargetDataSourceKey(defaultTargetDataSourceKey);
        DdlInterceptor.dataSourceDbMap = dataSourceDbMap;
        DdlInterceptor.dbDataSourceMap = dbDataSourceMap;
        dynamicDataSource.initDdlConfig();
        dynamicDataSource.initSqlLog();
        dynamicDataSource.afterPropertiesSet();
    }
}
