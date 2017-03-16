package org.hisoka.orm.relative.apply;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/20
 * @copyright: 2016 All rights reserved.
 */
public class DataSourceSwitcher {

    private static final ThreadLocal<String> dbContextHolder = new ThreadLocal<String>();
    private static final ThreadLocal<Boolean> dbContextReadWriteSeparate = new ThreadLocal<Boolean>();

    public static void setDataSourceTypeInContext(String dataSourceType) {
        dbContextHolder.set(dataSourceType);
        dbContextReadWriteSeparate.set(true);
    }

    /**
     * 强制设置当前数据源到主库数据源上下文中，即本次线程生命周期内读请求都走主库(
     * 即使读写分离开关readWriteSeparateFlag为true读请求也会强制走主库)
     *
     */
    public static void setDataSourceTypeForceMaster() {
        setDataSourceTypeForceMaster(null);
    }

    /**
     * 强制设置数据源到主库数据源上下文中，即本次线程生命周期内读请求都走主库(
     * 即使读写分离开关readWriteSeparateFlag为true读请求也会强制走主库)
     *
     * @param dataSourceType
     */
    public static void setDataSourceTypeForceMaster(String dataSourceType) {
        if (dataSourceType != null) {
            dbContextHolder.set(dataSourceType);
        }

        dbContextReadWriteSeparate.set(false);
    }

    /**
     * 强制设置当前数据源到备库数据源上下文中，即本次线程生命周期内读请求都走备库(
     * 即使读写分离开关readWriteSeparateFlag为false读请求也会强制走备库)
     *
     */
    public static void setDataSourceTypeForceSlave() {
        setDataSourceTypeForceSlave(null);
    }

    /**
     * 强制设置数据源到备库数据源上下文中，即本次线程生命周期内读请求都走备库(
     * 即使读写分离开关readWriteSeparateFlag为false读请求也会强制走备库)
     *
     * @param dataSourceType
     */
    public static void setDataSourceTypeForceSlave(String dataSourceType) {
        if (dataSourceType != null) {
            dbContextHolder.set(dataSourceType);
        }

        dbContextReadWriteSeparate.set(true);
    }

    public static String getDataSourceType() {
        // 目前支持从线程上下文中获取，以后提供扩展接口，允许用户实现具体的获取方式
        return getDataSourceTypeFromContext();
    }

    public static String getDataSourceTypeFromContext() {
        String dataSourceType = (String) dbContextHolder.get();
        return dataSourceType;
    }

    public static Boolean getReadWriteSeparateFromContext() {
        Boolean readWriteSeparateFlag = dbContextReadWriteSeparate.get();
        return readWriteSeparateFlag;
    }

    public static void clearDataSourceType() {
        dbContextHolder.remove();
        dbContextReadWriteSeparate.remove();
    }

}
