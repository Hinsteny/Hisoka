package org.hisoka.orm.relative.ddl.interceptor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import org.hisoka.common.exception.SystemException;
import org.hisoka.common.util.other.ConsistenHashUtil;
import org.hisoka.common.util.string.StringUtil;
import org.hisoka.orm.relative.apply.DataSourceSwitcher;
import org.hisoka.orm.relative.apply.DynamicDataSource;
import org.hisoka.orm.relative.ddl.DdlConfig;
import org.hisoka.orm.relative.ddl.DdlDb;
import org.hisoka.orm.relative.ddl.DdlTable;
import org.hisoka.orm.relative.ddl.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/20
 * @copyright: 2016 All rights reserved.
 */
public class DdlInterceptor {


    protected final Logger log = LoggerFactory.getLogger(DdlInterceptor.class);

    public static final String TABLE_REGULAR_EXPRESSION_PREFIX = "(?:\\.|`|,|\\s)?";

    public static final String TABLE_REGULAR_EXPRESSION_SUFFIX = "(?:\\.|`|,|\\s|$)";

    public static Map<String, DdlConfig> ddlConfigMap;

    public static String tablePattern;

    public static Map<String, ConsistenHashUtil<DdlTable>> consistenHashUtilMap;

    public static Map<String, String> dataSourceDbMap;

    public static Map<String, DdlDb> dbDataSourceMap;

    protected CCJSqlParserManager parserManager = new CCJSqlParserManager();

    protected static boolean ddlFlag;

    protected static boolean readWriteSeparateFlag;

    public static void setDdlFlag(boolean ddlFlag) {
        DdlInterceptor.ddlFlag = ddlFlag;
    }

    public static void setReadWriteSeparateFlag(boolean readWriteSeparateFlag) {
        DdlInterceptor.readWriteSeparateFlag = readWriteSeparateFlag;
    }

    /**
     *
     * @param interceptSql
     * @return
     */
    protected boolean getDdlFlag(String interceptSql, List<String> tableList) {
        Set<String> tableSet = ddlConfigMap.keySet();
        String patternString = "[\\s\\S]*(" + StringUtil.join(tableSet, "|") + ")[\\s\\S]*";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(interceptSql);

        if (interceptSql != null && !"".equals(interceptSql) && matcher.matches()) {
            if (tableList == null || tableList.isEmpty()) {
                return false;
            }

            Iterator<String> iterator = tableSet.iterator();

            while (iterator.hasNext()) {
                if (tableList.contains(iterator.next())) {
                    return true;
                }
            }
        }

        return false;
    }

    protected List<String> getTableList(String interceptSql) {
        String sql = interceptSql.replaceAll("[\\s]+", " ").trim();
        Statement statement = null;
        List<String> tableList = null;

        try {
            statement = parserManager.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
        }

        if (statement != null) {
            if (statement instanceof Select) {
                Select select = (Select) statement;
                TableView tableView = new TableView();
                tableList = new ArrayList<String>();
                tableList.addAll(tableView.getTableList(select));
            } else if (statement instanceof Insert) {
                Insert insert = (Insert) statement;
                Table table = insert.getTable();
                tableList = new ArrayList<String>();
                tableList.add(table.getName());
            } else if (statement instanceof Update) {
                Update update = (Update) statement;
                List<Table> tables = update.getTables();
                tableList = new ArrayList<String>();
                for (Table table:tables) {
                    tableList.add(table.getName());
                }
            } else if (statement instanceof Delete) {
                Delete delete = (Delete) statement;
                Table table = delete.getTable();
                tableList = new ArrayList<String>();
                tableList.add(table.getName());
            } else if (statement instanceof CreateTable) {
                CreateTable createTable = (CreateTable) statement;
                Table table = createTable.getTable();
                tableList = new ArrayList<String>();
                tableList.add(table.getName());
            } else if (statement instanceof Drop) {
                Drop drop = (Drop) statement;
                String type = drop.getType();
                if ("TABLE".equals(type)) {
                    tableList = new ArrayList<String>();
                    tableList.add(drop.getName().getName());
                }
            } else if (statement instanceof Replace) {
                Replace replace = (Replace) statement;
                Table table = replace.getTable();
                tableList = new ArrayList<String>();
                tableList.add(table.getName());
            } else if (statement instanceof Truncate) {
                Truncate truncate = (Truncate) statement;
                Table table = truncate.getTable();
                tableList = new ArrayList<String>();
                tableList.add(table.getName());
            }
        } else if (sql.toUpperCase().indexOf("SHOW CREATE TABLE") != -1) {
            String patternString = "SHOW CREATE TABLE\\s+(\\S+)";
            Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sql);
            tableList = new ArrayList<String>();

            while (matcher.find()) {
                String table = matcher.group(1).trim();
                tableList.add(StringUtil.trimString(table, "`"));
            }
        } else if (sql.toUpperCase().indexOf("INSERT INTO") != -1) {
            String patternString = "INSERT INTO\\s+(\\S+)\\s+";
            Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sql);
            tableList = new ArrayList<String>();

            while (matcher.find()) {
                String table = matcher.group(1).trim();
                tableList.add(StringUtil.trimString(table, "`"));
            }
        }

        return tableList;
    }

    /**
     * 获取某列的值
     *
     * @param sql
     * @param columnName
     * @return
     * @throws JSQLParserException
     */
    protected Object getColumnValue(String sql, String columnName) {
        Statement statement = null;

        try {
            statement = parserManager.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            log.warn("Parse sql exception, " + sql, e);
        }

        if (statement != null) {
            if (statement instanceof Insert) {
                Insert insert = (Insert) statement;
                List<?> columnList = insert.getColumns();

                for (int i = 0; i < columnList.size(); i++) {
                    Column column = (Column) columnList.get(i);

                    if (columnName.equals(column.getColumnName())) {
                        ItemsList itemsList = insert.getItemsList();

                        if (itemsList instanceof ExpressionList) {
                            ExpressionList expressionList = (ExpressionList) itemsList;

                            if (expressionList.getExpressions().get(i) != null) {
                                String columnValue = expressionList.getExpressions().get(i).toString();
                                return columnValue.replaceAll("^[\"|']|[\"|']$", "");
                            } else {
                                return null;
                            }
                        }
                    }
                }
            } else {
                String patternString = columnName + "\\s=\\s(.*?)[\\s|;]";
                Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(sql + " ");

                while (matcher.find()) {
                    String columnValue = matcher.group(1).trim();
                    return columnValue.replaceAll("^[\"|']|[\"|']$", "");
                }
            }
        }

        return null;
    }

    public static String getDataSourceByDb(String db, boolean isSlave) {
        if (dbDataSourceMap == null) {
            throw new SystemException("Can not find a db, because dbDataSourceMap is null.");
        } else {
            DdlDb ddlDb = dbDataSourceMap.get(db);

            if (ddlDb == null) {
                throw new SystemException("Can not find a dataSource by db " + db + " from dbDataSourceMap.");
            }

            if (isSlave) {
                List<String> slaveDataSourceKeyList = ddlDb.getSlaveDataSourceKeyList();

                if (slaveDataSourceKeyList != null && !slaveDataSourceKeyList.isEmpty()) {
                    Random random = new Random();
                    int size = slaveDataSourceKeyList.size();
                    String dataSource = slaveDataSourceKeyList.get(random.nextInt(size));

                    if (dataSource != null) {
                        return dataSource;
                    }
                }
            }

            String dataSource = ddlDb.getMasterDataSourceKey();
            return dataSource;
        }
    }

    public String getSlaveDataSourceByMasterDataSource(String masterDataSource) {
        if (StringUtil.isBlank(masterDataSource)) {
            return null;
        }

        String db = getDbByDataSource(masterDataSource);
        String slaveDataSource = getDataSourceByDb(db, true);
        return slaveDataSource;
    }

    public static String getDbByDataSource(String dataSource) {
        if (dataSourceDbMap == null) {
            throw new SystemException("Can not find a db, because dataSourceDbMap is null.");
        } else {
            String db = dataSourceDbMap.get(dataSource);

            if (db == null) {
                throw new SystemException("Can not find a db by dataSource=" + dataSource + " from dataSourceDbMap.");
            }

            return db;
        }
    }

    public static boolean getReadWriteSeparate(boolean readWriteSeparateFlag) {
        Boolean readWriteSeparate = null;

        if (readWriteSeparateFlag) {
            readWriteSeparate = DataSourceSwitcher.getReadWriteSeparateFromContext();

            if (readWriteSeparate == null) {
                return true;
            } else {
                return readWriteSeparateFlag;
            }
        } else {
            readWriteSeparate = DataSourceSwitcher.getReadWriteSeparateFromContext();

            if (readWriteSeparate == null) {
                return false;
            } else {
                return readWriteSeparateFlag;
            }
        }
    }

    public static String getCurrentDataSource(DynamicDataSource dynamicDataSource) {
        String currentDataSource = dynamicDataSource.getCurrentDataSource();
        return currentDataSource;
    }

    public static String getCurrentDb(DynamicDataSource dynamicDataSource) {
        String currentDataSource = getCurrentDataSource(dynamicDataSource);
        return getDbByDataSource(currentDataSource);
    }

    public static String getTablePattern(Set<String> keySet) {
        if (keySet == null || keySet.isEmpty()) {
            return "";
        }

        Iterator<String> iterator = keySet.iterator();
        String first = iterator.next();

        if (!iterator.hasNext()) {
            return TABLE_REGULAR_EXPRESSION_PREFIX + "(" + first + ")" + TABLE_REGULAR_EXPRESSION_SUFFIX;
        }

        StringBuffer buf = new StringBuffer();
        buf.append(TABLE_REGULAR_EXPRESSION_PREFIX);
        buf.append("(");

        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            String table = iterator.next();
            buf.append("|");

            if (table != null) {
                buf.append(table);
            }
        }

        buf.append(")");
        buf.append(TABLE_REGULAR_EXPRESSION_SUFFIX);
        return buf.toString();
    }

}
