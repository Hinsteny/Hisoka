package org.hisoka.web.controller;

import org.hisoka.common.exception.SystemException;
import org.hisoka.common.util.date.DateUtil;
import org.hisoka.common.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class BaseController {
    /**
     * 日志
     */
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    /**
     * jsonp安全处理
     *
     * @return
     */
    protected String jsonpSecurityFilter(String jsonp) {
        // TODO

        return null;
    }

    /**
     * 判断您是否有xss攻击
     *
     * @return
     */
    protected boolean isSecurity(String value) {
        // TODO
        return true;
    }

    protected String getStringParameter(String name) {
        return getStringParameter(name, null);
    }

    protected String getStringParameter(String name, String defaultValue) {
        String value = getHttpServletRequest().getParameter(name);

        return StringUtil.isEmpty(value) ? defaultValue : value.trim();
    }

    protected BigDecimal getBigDecimalParameter(String name) {
        String value = getHttpServletRequest().getParameter(name);
        return StringUtil.isEmpty(value) ? null : new BigDecimal(value);
    }

    protected BigDecimal getBigDecimalParameter(String name, String defaultValue) {
        String value = getHttpServletRequest().getParameter(name);
        return StringUtil.isEmpty(value) ? new BigDecimal(defaultValue) : new BigDecimal(value);
    }

    protected Boolean getBooleanParameter(String name) {
        return getBooleanParameter(name, null);
    }

    protected Boolean getBooleanParameter(String name, Boolean defaultValue) {
        String value = getHttpServletRequest().getParameter(name);
        return StringUtil.isEmpty(value) ? defaultValue : Boolean.valueOf(value);
    }

    protected Integer getIntegerParameter(String name) {
        return getIntegerParameter(name, null);
    }

    protected Integer getIntegerParameter(String name, Integer defaultValue) {
        String value = getHttpServletRequest().getParameter(name);
        return StringUtil.isEmpty(value) ? defaultValue : Integer.valueOf(value);
    }

    protected Long getLongParameter(String name) {
        return getLongParameter(name, null);
    }

    protected Long getLongParameter(String name, Long defaultValue) {
        String value = getHttpServletRequest().getParameter(name);
        return StringUtil.isEmpty(value) ? defaultValue : Long.valueOf(value);
    }

    protected Float getFloatParameter(String name) {
        return getFloatParameter(name, null);
    }

    protected Float getFloatParameter(String name, Float defaultValue) {
        String value = getHttpServletRequest().getParameter(name);
        return StringUtil.isEmpty(value) ? defaultValue : Float.valueOf(value);
    }

    protected Double getDoubleParameter(String name) {
        return getDoubleParameter(name, null);
    }

    protected Double getDoubleParameter(String name, Double defaultValue) {
        String value = getHttpServletRequest().getParameter(name);
        return StringUtil.isEmpty(value) ? defaultValue : Double.valueOf(value);
    }

    protected Date getDateParameter(String name) {
        return getDateParameter(name, DateUtil.LONG_DATE_FORMAT_STR, null);
    }

    protected Date getDateParameter(String name, String format) {
        return getDateParameter(name, format, null);
    }

    protected Date getDateParameter(String name, String format, String defaultValue) {
        String value = getHttpServletRequest().getParameter(name);
        DateFormat dateFormat = new SimpleDateFormat(format);

        try {
            if (StringUtil.isBlank(value)) {
                return null == defaultValue ? null : dateFormat.parse(defaultValue);
            } else {
                if (StringUtil.isEmpty(format)) {
                    Long timeMillis = null;
                    try {
                        timeMillis = Long.parseLong(value);
                    } catch (NumberFormatException e) {
                    }
                    if (timeMillis != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timeMillis);
                        return calendar.getTime();
                    } else {
                        return null;
                    }
                } else {
                    return dateFormat.parse(value);
                }
            }
        } catch (ParseException e) {
            throw new SystemException("时间格式有误");
        }
    }

    protected MultipartFile getMultipartFile(HttpServletRequest request, String fileName) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        return multipartRequest.getFile(fileName);
    }

}
