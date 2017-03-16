package org.hisoka.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hisoka.common.constance.Application;
import org.hisoka.common.constance.Result;
import org.hisoka.common.util.date.DateUtil;
import org.hisoka.common.util.http.URLUtil;
import org.hisoka.common.util.other.CommonUtil;
import org.hisoka.common.util.other.IPUtil;
import org.hisoka.common.util.other.LogUtil;
import org.hisoka.common.util.string.StringUtil;
import org.hisoka.core.session.config.SessionConfig;
import org.hisoka.web.domain.RequestLog;
import org.hisoka.web.domain.ResponseLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public class LogInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    private static final String DEFAULT_SESSION_NAME = "JSESSIONID";

    private static final String MODEL_VIEW_OBJECT = "org.springframework.validation";

    private static final String MODEL_VIEW_CONTENT_TYPE = "text/html;charset=UTF-8";

    /**
     * 日志开关，默认为false不打开
     */
    private boolean openLog = false;

    /**
     * 日志最大长度，如果不传则默认1000，传-1则不限制日志打印长度
     */
    private int logLength;

    @Autowired(required = false)
    private SessionConfig sessionConfig;

    public void setOpenLog(boolean openLog) {
        this.openLog = openLog;
    }

    public void setLogLength(int logLength) {
        this.logLength = logLength;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (openLog) {
            try {
                RequestLog requestLog = getRequestLog(request);
                request.setAttribute("requestLog", requestLog);
                String log = getLog(requestLog);
                LogUtil.log(LOG, log);
            } catch (Exception e) {
                LOG.error("LogInterceptor preHandle error", e);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (openLog) {
            try {
                ResponseLog responseLog = getResponseLog(request, response, modelAndView);
                request.setAttribute("responseLog", responseLog);
            } catch (Exception e) {
                LOG.error("LogInterceptor postHandle error", e);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (openLog) {
            try {
                Object requestObj = request.getAttribute("requestLog");
                Object responseObj = request.getAttribute("responseLog");
                RequestLog requestLog = null;
                ResponseLog responseLog = null;

                if (requestObj != null) {
                    requestLog = (RequestLog) requestObj;
                }

                if (responseObj != null) {
                    responseLog = (ResponseLog) responseObj;
                }

                if (requestLog == null) {
                    requestLog = getRequestLog(request);
                }

                if (responseLog == null) {
                    responseLog = getResponseLog(request, response, null);
                }

                Date start = (requestLog.getTime()) != null ? requestLog.getTime() : new Date();
                Date end = (responseLog.getTime()) != null ? responseLog.getTime() : new Date();
                String cost = end.getTime() - start.getTime() + "ms";
                responseLog.setCost(cost);
                String log = getLog(responseLog);
                int logLength = this.logLength != 0 ? this.logLength : Application.LOG_MAX_LENGTH;

                if (logLength != -1 && log.length() > logLength) {
                    log = log.substring(0, logLength);
                }

                LogUtil.log(LOG, log);
            } catch (Throwable t) {
                LOG.error("LogInterceptor afterCompletion error", t);
            }
        }
    }

    private String getLog(RequestLog requestLog) {
        String log = "[HttpRequest] "
                + JSON.toJSONStringWithDateFormat(requestLog, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
        return log;
    }

    private String getLog(ResponseLog responseLog) {
        String log = "[HttpResponse] "
                + JSON.toJSONStringWithDateFormat(responseLog, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
        return log;
    }

    private RequestLog getRequestLog(HttpServletRequest request) {
        Date time = new Date();
        String url = new String(request.getRequestURL());
        String addr = URLUtil.getClientAddr(request);
        String referer = request.getHeader("Referer");
        String accept = request.getHeader("Accept");
        String agent = request.getHeader("User-Agent");
        String contentType = request.getContentType();
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        String sessionName = (sessionConfig != null) ? sessionConfig.getName() : DEFAULT_SESSION_NAME;

        if (cookies != null && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];

                if (sessionName.equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        RequestLog requestLog = new RequestLog();
        requestLog.setUrl(url);
        requestLog.setReferer(referer);
        requestLog.setAddr(addr);
        requestLog.setAccept(accept);
        requestLog.setAgent(agent);
        requestLog.setContentType(contentType);
        requestLog.setSessionId(sessionId);
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> parameters = new LinkedHashMap<String, String>();

        if (parameterMap != null && !parameterMap.isEmpty()) {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String[] valueArray = entry.getValue();
                String value = CommonUtil.getStringArrayStr(valueArray, ",");
                parameters.put(key, value);
            }
        }

        requestLog.setParameters(parameters);
        requestLog.setTime(time);
        return requestLog;
    }

    private ResponseLog getResponseLog(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {
        ResponseLog responseLog = new ResponseLog();
        String contentType = response.getContentType();
        String addr = IPUtil.getLocalIp();

        if (StringUtil.isBlank(contentType)) {
            contentType = response.getHeader("Content-Type");
        }

        String setCookie = response.getHeader("Set-Cookie");

        if (modelAndView != null) {
            if (StringUtil.isBlank(contentType)) {
                contentType = MODEL_VIEW_CONTENT_TYPE;
            }

            String viewName = modelAndView.getViewName();
            responseLog.setContentType(contentType);
            responseLog.setSetCookie(setCookie);
            responseLog.setView(viewName);
            responseLog.setAddr(addr);
            Map<String, Object> map = modelAndView.getModel();
            Map<String, Object> result = new LinkedHashMap<String, Object>();

            if (map != null && !map.isEmpty()) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (key.contains(MODEL_VIEW_OBJECT) || value instanceof BeanPropertyBindingResult) {
                        continue;
                    }

                    result.put(key, value);
                }

                responseLog.setResult(result);
            }
        } else {
            Result result = (Result) request.getAttribute("result");
            String callback = (String) request.getAttribute("callback");
            responseLog.setContentType(contentType);
            responseLog.setSetCookie(setCookie);
            responseLog.setResult(result);
            responseLog.setCallback(callback);
            responseLog.setAddr(addr);
        }

        responseLog.setTime(new Date());
        return responseLog;
    }

}