package org.hisoka.common.util.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class HttpSessionProxy implements HttpSession, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7411463742758221896L;

    private static final String defaultKeyPre = "@BerbonSessClt.";

    /**
     * 创建时间，单位(毫秒)
     */
    protected long creationTime = 0l;

    protected String id;

    /**
     * 最大间隔时间，单位(毫秒)
     */
    protected int maxInactiveInterval;

    /**
     * 上一次访问时间，单位(毫秒)
     */
    protected long lastAccessedTime = 0l;

    protected transient boolean expired = false;

    protected transient boolean isNew = false;

    protected transient boolean isDirty = false;

    protected ConcurrentMap<String, Object> data = new ConcurrentHashMap<String, Object>();

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return this.data.get(commpleteAttrKey(name));
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        final Iterator<String> localIterator = this.data.keySet().iterator();
        return new Enumeration<String>() {
            public boolean hasMoreElements() {
                return localIterator.hasNext();
            }

            public String nextElement() {
                return localIterator.next();
            }
        };
    }

    @Override
    public String[] getValueNames() {
        String[] arrayOfString = new String[this.data.size()];
        return (String[]) this.data.keySet().toArray(arrayOfString);
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.data.put(commpleteAttrKey(name), value);
        this.isDirty = true;
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.data.remove(commpleteAttrKey(name));
        this.isDirty = true;
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        this.expired = true;
        this.isDirty = true;
    }

    public boolean isExpired() {
        return expired;
    }

    public boolean getExpired() {
        return getLastAccessedTime() + getMaxInactiveInterval() <= System.currentTimeMillis();
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public ConcurrentMap<String, Object> getData() {
        return data;
    }

    private String commpleteAttrKey(String name) {
        if (name == null) {
            return name;
        }
        Pattern pattern = Pattern.compile("^@[a-zA-Z0-9]*.[a-zA-Z0-9.]*$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            return name;
        } else {
            return defaultKeyPre + name;
        }
    }

    @Override
    public String toString() {
        return "HttpSessionProxy [creationTime=" + creationTime + ", id=" + id + ", maxInactiveInterval=" + maxInactiveInterval + ", lastAccessedTime="
                + lastAccessedTime + ", expired=" + expired + ", isNew=" + isNew + ", isDirty=" + isDirty + ", data=" + data + "]";
    }

}
