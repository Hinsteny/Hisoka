package org.hisoka.orm.relative.apply;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hisoka.common.exception.SystemException;
import org.hisoka.common.util.other.ConsistenHashUtil;
import org.hisoka.common.util.string.StringUtil;
import org.hisoka.core.context.SpringContextHolder;
import org.hisoka.orm.relative.ddl.DdlConfig;
import org.hisoka.orm.relative.ddl.DdlDb;
import org.hisoka.orm.relative.ddl.interceptor.DdlInterceptor;
import org.hisoka.orm.relative.ddl.DdlTable;
import org.hisoka.orm.relative.interceptor.InterceptorUtil;
import org.hisoka.orm.relative.interceptor.SqlLogInterceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/20
 * @copyright: 2016 All rights reserved.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final String DDL_DB_CLASS = "org.hisoka.orm.relative.ddl.interceptor.DdlDbInterceptor";

    private static final String DDL_TABLE_CLASS = "org.hisoka.orm.relative.ddl.interceptor.DdlTableInterceptor";

    private static final String SQL_LOG_CLASS = "org.hisoka.orm.relative.interceptor.SqlLogInterceptor";

    private static final String DEFAULT_DDL_FILE = "ddl-config.xml";

    private static Map<String, String> dataSourceDbMap;

    private static Map<String, DdlDb> dbDataSourceMap;

    private Map<Object, Object> dataSourceMap;

    private String defaultTargetDataSourceKey;

    private String currentDataSource;

    private String ddlFile;

    private boolean ddlFlag;

    /**
     * 读写分离开关，默认为false(true-从库读取，false-主库读取)
     */
    private boolean readWriteSeparateFlag = false;

    /**
     * 日志开关，默认为false不打开
     */
    private boolean openLog = false;

    /**
     * 日志最大长度，如果不传则默认1000，传-1则不限制日志打印长度
     */
    private int logLength;

    private String ignorePattern;

    private long slowLimit = 1000l;

    public Map<String, String> getDataSourceDbMap() {
        return dataSourceDbMap;
    }

    public void setDataSourceDbMap(Map<String, String> dataSourceDbMap) {
        DynamicDataSource.dataSourceDbMap = dataSourceDbMap;
    }

    public Map<String, DdlDb> getDbDataSourceMap() {
        return dbDataSourceMap;
    }

    public void setDbDataSourceMap(Map<String, DdlDb> dbDataSourceMap) {
        DynamicDataSource.dbDataSourceMap = dbDataSourceMap;
    }

    public void setDefaultTargetDataSourceKey(String defaultTargetDataSourceKey) {
        this.defaultTargetDataSourceKey = defaultTargetDataSourceKey;
    }

    public void setDataSourceMap(Map<Object, Object> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
    }

    public void setDdlFile(String ddlFile) {
        this.ddlFile = ddlFile;
    }

    public void setDdlFlag(boolean ddlFlag) {
        this.ddlFlag = ddlFlag;
    }

    public void setReadWriteSeparateFlag(boolean readWriteSeparateFlag) {
        this.readWriteSeparateFlag = readWriteSeparateFlag;
    }

    public void setOpenLog(boolean openLog) {
        this.openLog = openLog;
    }

    public void setLogLength(int logLength) {
        this.logLength = logLength;
    }

    public void setIgnorePattern(String ignorePattern) {
        this.ignorePattern = ignorePattern;
    }

    public void setSlowLimit(long slowLimit) {
        this.slowLimit = slowLimit;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSource = DataSourceSwitcher.getDataSourceType();

        if (dataSource == null) {
            dataSource = defaultTargetDataSourceKey;
        }

        if (dataSource == null) {
            throw new SystemException("Can not find a dataSource.");
        }

        this.currentDataSource = dataSource;
        return dataSource;
    }

    /**
     * 根据db获取数据源，如果isSlave为true则获取备库数据源，否则则获取主库数据源(注：如果备库数据源获取不到则使用主库数据源代替)
     *
     * @param db 数据库名
     * @param isSlave 是否获取备库
     * @return 数据源
     */
    public static String getDataSourceByDb(String db, boolean isSlave) {
        return DdlInterceptor.getDataSourceByDb(db, isSlave);
    }

    /**
     *
     * @return
     */
    public String getCurrentDataSource() {
        return this.currentDataSource;
    }

    public Set<String> getDataSources() {
        return dataSourceDbMap.keySet();
    }

    public DataSource getDataSource(String dataSourceKey) {
        DataSource dataSource = (DataSource) dataSourceMap.get(dataSourceKey);
        return dataSource;
    }

    public void initSqlLog() {
        if (openLog) {
            SqlLogInterceptor.setOpenLog(openLog);
            SqlLogInterceptor.setSlowLimit(slowLimit);
            SqlLogInterceptor.setLogLength(logLength);
            SqlLogInterceptor.setIgnorePattern(ignorePattern);
            InterceptorUtil.setSqlSessionFactoryBean(SpringContextHolder.applicationContext.getBean(SqlSessionFactoryBean.class));
            InterceptorUtil.dynamicAddInterceptor(SQL_LOG_CLASS);
        }
    }

    /**
     * 初始化ddl配置，使用一致性Hash算法
     *
     * @throws Exception
     */
    public void initDdlConfig() {
        if (ddlFlag) {
            try {
                DdlInterceptor.setDdlFlag(ddlFlag);
                DdlInterceptor.setReadWriteSeparateFlag(readWriteSeparateFlag);
                InterceptorUtil.setSqlSessionFactoryBean(SpringContextHolder.applicationContext.getBean(SqlSessionFactoryBean.class));
                InterceptorUtil.dynamicAddInterceptor(DDL_DB_CLASS);
                InterceptorUtil.dynamicAddInterceptor(DDL_TABLE_CLASS);
                String ddlFile = StringUtil.isNotBlank(this.ddlFile) ? this.ddlFile : DEFAULT_DDL_FILE;
                URL url = Thread.currentThread().getContextClassLoader().getResource(ddlFile);

                if (url != null) {
                    SAXReader saxReader = new SAXReader();
                    Document document = null;

                    if (isJarUrl(url)) {
                        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ddlFile);
                        document = saxReader.read(is);
                    } else {
                        document = saxReader.read(url);
                    }

                    Element root = document.getRootElement();
                    Element tables = root.element("tables");
                    List<?> nodes = tables.elements("table");
                    Iterator<?> iterator = nodes.iterator();

                    if (DdlInterceptor.ddlConfigMap == null) {
                        DdlInterceptor.ddlConfigMap = new HashMap<String, DdlConfig>();
                    }

                    if (DdlInterceptor.dataSourceDbMap == null || DdlInterceptor.dataSourceDbMap.isEmpty() || DdlInterceptor.dbDataSourceMap == null
                            || DdlInterceptor.dbDataSourceMap.isEmpty()) {
                        buildDdlMap();
                    }

                    List<DdlTable> ddlTableList = null;

                    while (iterator.hasNext()) {
                        ddlTableList = new ArrayList<DdlTable>();
                        Element e = (Element) iterator.next();
                        String table = e.attributeValue("table").trim();
                        String column = e.attributeValue("column") == null ? null : e.attributeValue("column").trim();
                        Integer tableNum = e.attributeValue("tableNum") == null ? null : Integer.valueOf(e.attributeValue("tableNum").trim());
                        String db = e.attributeValue("db") == null ? null : e.attributeValue("db").trim();
                        Integer dbNum = e.attributeValue("dbNum") == null ? null : Integer.valueOf(e.attributeValue("dbNum").trim());
                        DdlInterceptor.ddlConfigMap.put(table, new DdlConfig(table, column, tableNum, db, dbNum));

                        if (tableNum != null && tableNum > 1) {
                            for (int i = 1; i <= tableNum; i++) {
                                if (dbNum == null || dbNum == 1) {
                                    ddlTableList.add(new DdlTable(table + "_" + i, 0));
                                } else {
                                    if (dbNum > tableNum) {
                                        throw new SystemException("Ddl config error, tableNum should max than dbNum");
                                    }

                                    ddlTableList.add(new DdlTable(table + "_" + i, (i - 1) / (tableNum / dbNum) + 1));
                                }
                            }

                            if (DdlInterceptor.consistenHashUtilMap == null) {
                                DdlInterceptor.consistenHashUtilMap = new HashMap<String, ConsistenHashUtil<DdlTable>>();
                            }

                            DdlInterceptor.consistenHashUtilMap.put(table, new ConsistenHashUtil<DdlTable>(ddlTableList));
                        }
                    }

                    DdlInterceptor.tablePattern = DdlInterceptor.getTablePattern(DdlInterceptor.ddlConfigMap.keySet());
                }
            } catch (Exception e) {
                throw new SystemException("InitDdlConfig error", e);
            }
        }
    }

    private boolean isJarUrl(URL url) {
        if (url.toString().indexOf("jar:file") != -1 || url.toString().indexOf("jar!") != -1) {
            return true;
        } else {
            return false;
        }
    }

    private void buildDdlMap() {
        DdlInterceptor.dataSourceDbMap = getDataSourceDbMap();
        DdlInterceptor.dbDataSourceMap = getDbDataSourceMap();
    }
}
