package org.hisoka.orm.relative.ddl.interceptor;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.hisoka.common.util.other.ConsistenHashUtil;
import org.hisoka.common.util.reflect.ReflectUtil;
import org.hisoka.common.util.sql.SqlParserUtil;
import org.hisoka.orm.relative.ddl.DdlConfig;
import org.hisoka.orm.relative.ddl.DdlTable;
import org.hisoka.orm.relative.interceptor.SqlLogInterceptor;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/31
 * @copyright: 2016 All rights reserved.
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class DdlTableInterceptor extends DdlInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (ddlFlag) {
            RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String interceptSql = boundSql.getSql();
            List<String> tableList = getTableList(interceptSql);
            boolean ddlFlag = getDdlFlag(interceptSql, tableList);

            if (ddlFlag) {
                StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
                MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
                String standardSql = SqlParserUtil.handleSql(interceptSql, mappedStatement, boundSql);
                Map<String, String> ddlSqlMap = getDdlSqlMap(standardSql, interceptSql, tableList);
                String ddlSql = ddlSqlMap.get("ddlSql");
                String ddlStandardSql = ddlSqlMap.get("ddlStandardSql");

                if (StringUtils.isNotBlank(ddlSql)) {
                    ReflectUtil.setFieldValue(boundSql, "sql", ddlSql);
                    SqlLogInterceptor.setExecuteSql(ddlStandardSql);
                }
            }
        }

        Object obj = invocation.proceed();
        return obj;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 获取ddl的db
     *
     * @param sql
     * @return
     */
    private Map<String, String> getDdlSqlMap(String sql, String interceptSql, List<String> tableList) {
        // Pattern pattern = Pattern.compile(tablePattern,
        // Pattern.CASE_INSENSITIVE);
        // Matcher matcher = pattern.matcher(interceptSql);
        // StringBuffer sb = new StringBuffer();
        Map<String, String> ddlSqlMap = new HashMap<String, String>();
        String ddlSql = interceptSql;
        String ddlStandardSql = sql;
        Iterator<String> iterator = tableList.iterator();
        Set<String> ddlTableSet = ddlConfigMap.keySet();

        while (iterator.hasNext()) {
            String table = iterator.next();

            if (ddlTableSet.contains(table)) {
                DdlConfig ddlConfig = ddlConfigMap.get(table);

                if (ddlConfig == null) {
                    continue;
                }

                String column = ddlConfig.getColumn();

                if (StringUtils.isBlank(column)) {
                    continue;
                }

                ConsistenHashUtil<DdlTable> consistenHashUtil = consistenHashUtilMap.get(table);

                if (consistenHashUtil != null) {
                    Object columnValue = null;

                    try {
                        columnValue = getColumnValue(sql, ddlConfig.getColumn());
                    } catch (Exception e) {
                        log.error("Get columnValue error", e);
                    }

                    if (columnValue != null) {
                        DdlTable ddlTable = consistenHashUtil.get(columnValue);
                        String ddlTableName = ddlTable.getDdlTableName();
                        String regex1 = "\\s+" + table + "\\s+";
                        String regex2 = "\\s+" + table + "\\.";
                        ddlSql = ddlSql.replaceAll(regex1, " " + ddlTableName + " ");
                        ddlSql = ddlSql.replaceAll(regex2, " " + ddlTableName + ".");
                        ddlStandardSql = ddlStandardSql.replaceAll(regex1, " " + ddlTableName + " ");
                        ddlStandardSql = ddlStandardSql.replaceAll(regex2, " " + ddlTableName + ".");
                    }
                }
            }
        }

        ddlSqlMap.put("ddlSql", ddlSql);
        ddlSqlMap.put("ddlStandardSql", ddlStandardSql);
        return ddlSqlMap;
        // // 正则表达式方式考虑不完善，暂时不使用
        // while (matcher.find()) {
        // String table = matcher.group(1).trim();
        //
        // if (!tableList.contains(table)) {
        // continue;
        // }
        //
        // DdlConfig ddlConfig = ddlConfigMap.get(table);
        //
        // if (ddlConfig == null) {
        // continue;
        // }
        //
        // String column = ddlConfig.getColumn();
        //
        // if (StringUtils.isBlank(column)) {
        // continue;
        // }
        //
        // ConsistenHashUtil<DdlTable> consistenHashUtil =
        // consistenHashUtilMap.get(table);
        //
        // if (consistenHashUtil != null) {
        // Object columnValue = null;
        //
        // try {
        // columnValue = getColumnValue(sql, ddlConfig.getColumn());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        //
        // if (columnValue != null) {
        // DdlTable ddlTable = consistenHashUtil.get(columnValue);
        // matcher.appendReplacement(sb, ddlTable.getDdlTableName());
        // }
        // }
        // }
        //
        // return matcher.appendTail(sb).toString();
    }

}

