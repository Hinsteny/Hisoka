package org.hisoka.orm.relative.ddl.service;

import org.hisoka.common.util.other.ConsistenHashUtil;
import org.hisoka.common.util.string.StringUtil;
import org.hisoka.orm.relative.apply.DynamicDataSource;
import org.hisoka.orm.relative.ddl.DdlConfig;
import org.hisoka.orm.relative.ddl.DdlRoute;
import org.hisoka.orm.relative.ddl.DdlTable;
import org.hisoka.orm.relative.ddl.interceptor.DdlInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/4
 * @copyright: 2016 All rights reserved.
 */
@Service("ddlRouteService")
public class DdlRouteService {

    @Autowired(required = false)
    private DynamicDataSource dynamicDataSource;

    public List<DdlConfig> getDdlConfigList() {
        List<DdlConfig> ddlConfigList = new ArrayList<DdlConfig>();

        for (Map.Entry<String, DdlConfig> en : DdlInterceptor.ddlConfigMap.entrySet()) {
            DdlConfig ddlConfig = en.getValue();
            ddlConfigList.add(ddlConfig);
        }

        return ddlConfigList;
    }

    public DdlRoute getDdlRoute(String table, String columnValue) {
        DdlConfig ddlConfig = DdlInterceptor.ddlConfigMap.get(table);

        if (ddlConfig == null) {
            return null;
        }

        String column = ddlConfig.getColumn();
        String ddlDbName = null;
        String ddlTableName = null;

        if (StringUtil.isBlank(column)) {
            ddlDbName = ddlConfig.getDb();
        } else {
            String db = ddlConfig.getDb();
            Integer dbNum = ddlConfig.getDbNum();

            if (StringUtil.isBlank(db) && (dbNum == null || dbNum == 1)) {
                ddlDbName = DdlInterceptor.getCurrentDb(dynamicDataSource);
            } else if (dbNum == null || dbNum == 1) {
                ddlDbName = db;
            }

            ConsistenHashUtil<DdlTable> consistenHashUtil = DdlInterceptor.consistenHashUtilMap.get(table);

            if (consistenHashUtil != null) {
                if (columnValue != null) {
                    DdlTable ddlTable = consistenHashUtil.get(columnValue);

                    if (ddlTable != null) {
                        Integer ddlDbNum = ddlTable.getDdlDbNum();

                        if (ddlDbNum != null && ddlDbNum != 0) {
                            if (StringUtil.isBlank(db)) {
                                String currentDb = DdlInterceptor.getCurrentDb(dynamicDataSource);

                                if (StringUtil.isNotBlank(currentDb)) {
                                    ddlDbName = currentDb + "_" + ddlDbNum;
                                }
                            } else {
                                ddlDbName = db + "_" + ddlDbNum;
                            }
                        }
                    }

                    ddlTableName = ddlTable.getDdlTableName();
                }
            }
        }

        if (StringUtil.isEmpty(ddlDbName)) {
            ddlDbName = DdlInterceptor.getCurrentDb(dynamicDataSource);
        }

        return new DdlRoute(ddlDbName, ddlTableName, column, columnValue);
    }

}

