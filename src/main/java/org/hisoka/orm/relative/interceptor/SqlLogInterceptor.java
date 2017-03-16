package org.hisoka.orm.relative.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.hisoka.common.constance.Application;
import org.hisoka.common.util.date.DateUtil;
import org.hisoka.common.util.other.LogUtil;
import org.hisoka.common.util.sql.SqlParserUtil;
import org.hisoka.common.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/20
 * @copyright: 2016 All rights reserved.
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class SqlLogInterceptor implements Interceptor {

    private static final ThreadLocal<String> sqlLogContextHolder = new ThreadLocal<String>();

    private static final long DEFAULT_SLOW_LIMIT = 1000l;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static boolean openLog;

    private static int logLength;

    private static long slowLimit;

    private static String ignorePattern;

    public static void setOpenLog(boolean openLog) {
        SqlLogInterceptor.openLog = openLog;
    }

    public static void setLogLength(int logLength) {
        SqlLogInterceptor.logLength = logLength;
    }

    public static void setSlowLimit(long slowLimit) {
        SqlLogInterceptor.slowLimit = slowLimit;
    }

    public static void setIgnorePattern(String ignorePattern) {
        SqlLogInterceptor.ignorePattern = ignorePattern;
    }

    public static String getExecuteSql() {
        String sql = sqlLogContextHolder.get();
        return sql;
    }

    public static void setExecuteSql(String sql) {
        sqlLogContextHolder.set(sql);
    }

    public static void clearSqlLogContext() {
        sqlLogContextHolder.remove();
    }

    // public static Boolean getDdlFlag() {
    // Boolean ddlFlag = ddlFlagContextHolder.get();
    // return ddlFlag;
    // }
    //
    // public static void setDdlFlag(Boolean ddlFlag) {
    // ddlFlagContextHolder.set(ddlFlag);
    // }
    //
    // public static void clearDdlFlagContext() {
    // ddlFlagContextHolder.remove();
    // }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (openLog) {
            long startTime = System.currentTimeMillis();
            long endTime = 0l;
            Object obj = null;

            try {
                obj = invocation.proceed();
            } catch (Throwable t) {
                obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
                throw t;
            } finally {
                MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
                String sqlId = mappedStatement.getId();
                boolean sqlLogFlag = getSqlLogFlag(sqlId);

                if (sqlLogFlag) {
                    String sql = getSql(invocation, mappedStatement);
                    endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
                    long cost = endTime - startTime;
                    long slowLimit = SqlLogInterceptor.slowLimit != 0l ? SqlLogInterceptor.slowLimit : DEFAULT_SLOW_LIMIT;
                    boolean slowQuery = (cost > slowLimit);
                    String result = "";

                    if (obj != null) {
                        result = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
                    }

                    boolean ddlFlag = getDdlFlag();
                    clearSqlLogContext();
                    String sqlLog = getSqlLog(sqlId, sql, result, slowQuery, ddlFlag, startTime, endTime, cost);
                    int logLength = SqlLogInterceptor.logLength != 0 ? SqlLogInterceptor.logLength : Application.LOG_MAX_LENGTH;

                    if (logLength != -1 && sqlLog.length() > logLength) {
                        sqlLog = sqlLog.substring(0, logLength);
                    }

                    LogUtil.log(log, sqlLog);
                }
            }

            return obj;
        } else {
            Object obj = invocation.proceed();
            return obj;
        }
    }

    private boolean getSqlLogFlag(String sqlId) {
        if (StringUtil.isBlank(ignorePattern)) {
            return true;
        }

        if (StringUtil.isBlank(sqlId)) {
            return true;
        }

        Pattern pattern = Pattern.compile(ignorePattern);
        Matcher matcher = pattern.matcher(sqlId);

        if (matcher.matches()) {
            return false;
        } else {
            return true;
        }
    }

    private String getSql(Invocation invocation, MappedStatement mappedStatement) {
        String sql = getExecuteSql();

        if (sql == null) {
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            String interceptSql = boundSql.getSql();
            sql = SqlParserUtil.handleSql(interceptSql, mappedStatement, boundSql);
        }

        return sql;
    }

    private Boolean getDdlFlag() {
        String sql = getExecuteSql();

        if (sql != null) {
            return true;
        } else {
            return false;
        }
    }

    private String getSqlLog(String method, String sql, String result, boolean slowQuery, boolean ddlFlag, long startTime, long endTime, long cost) {
        String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
        String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
        String slowQueryLog = slowQuery ? "(slowQuery)" : "";
        String ddlFlagLog = ddlFlag ? "(ddl)" : "";
        return String.format("[Sql]" + slowQueryLog + ddlFlagLog + " sql:%s|method:%s|result:%s|[start:%s, end:%s, cost:%dms]", sql, method, result,
                startTimeStr, endTimeStr, cost);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

}

