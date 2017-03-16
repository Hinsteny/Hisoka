package org.hisoka.core.session.config;

import org.hisoka.common.util.string.StringUtil;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public class SessionConfig {

    /**
     * session超时最大时间，默认30分钟，即1800秒(单位:秒)
     */
    private int expiredTime = 1800;

    /**
     * session保存到cookie中的名字
     */
    private String name = "JSESSIONID";

    /**
     * session保存到cookie中的域名
     */
    private String domain;

    /**
     * session保存到cookie中的路径
     */
    private String path;

    /**
     * 是否使用安全模式即https，默认否
     */
    private boolean secure = false;

    /**
     * 是否只支持http，默认否
     */
    private boolean httpOnly = false;

    /**
     * session在浏览器里的存储时间，默认永久
     */
    private int maxAge = -1;

    /**
     * 忽略的uri集合，使用","间隔
     */
    private String ignoreUris;

    /**
     * 忽略的请求后缀集合，使用","间隔
     */
    private String ignoreSuffixs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean getSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean getHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(int expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String[] getIgnoreUris() {
        String[] ignoreUriArray = null;

        if (StringUtil.isNotBlank(ignoreUris)) {
            ignoreUriArray = ignoreUris.split(",");
        }

        return ignoreUriArray;
    }

    public void setIgnoreUris(String ignoreUris) {
        this.ignoreUris = ignoreUris;
    }

    public String[] getIgnoreSuffixs() {
        String[] ignoreSuffixArray = null;

        if (StringUtil.isNotBlank(ignoreSuffixs)) {
            ignoreSuffixArray = ignoreSuffixs.split(",");
        }

        return ignoreSuffixArray;
    }

    public void setIgnoreSuffixs(String ignoreSuffixs) {
        this.ignoreSuffixs = ignoreSuffixs;
    }

    @Override
    public String toString() {
        return "SessionConfig [expiredTime=" + expiredTime + ", name=" + name + ", domain=" + domain + ", path=" + path + ", secure=" + secure + ", httpOnly="
                + httpOnly + ", maxAge=" + maxAge + ", ignoreUris=" + ignoreUris + ", ignoreSuffixs=" + ignoreSuffixs + "]";
    }

}
