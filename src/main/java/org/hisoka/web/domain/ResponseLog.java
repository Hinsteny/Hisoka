package org.hisoka.web.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
@JSONType(orders = { "addr", "result", "contentType", "setCookie", "view", "callback", "time", "cost" })
public class ResponseLog implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7748984325960374079L;

    @JSONField(name = "contentType")
    private String contentType;

    @JSONField(name = "view")
    private String view;

    @JSONField(name = "addr")
    private String addr;

    @JSONField(name = "callback")
    private String callback;

    @JSONField(name = "result")
    private Object result;

    @JSONField(name = "setCookie")
    private String setCookie;

    @JSONField(name = "time")
    private Date time;

    /**
     * 用时(单位: 毫秒)
     */
    @JSONField(name = "cost")
    private String cost;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getSetCookie() {
        return setCookie;
    }

    public void setSetCookie(String setCookie) {
        this.setCookie = setCookie;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "ResponseLog [contentType=" + contentType + ", view=" + view + ", addr=" + addr + ", callback=" + callback + ", result=" + result
                + ", setCookie=" + setCookie + ", time=" + time + ", cost=" + cost + "]";
    }

}

