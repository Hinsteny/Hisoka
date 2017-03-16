package org.hisoka.common.util.http;

import org.hisoka.common.exception.SystemException;
import org.hisoka.common.util.string.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public class URLUtil {

    private final static String ENVIRONMENT_GRAY = "GRAY_FLAG";

    private final static String ENVIRONMENT_GRAY_VALUE = "GRAY";

    /**
     * 获取服务全域名
     *
     * @param request
     * @return
     */
    public static final String getServerDomain(HttpServletRequest request) {
        return request.getServerName();
    }

    /**
     * 获取服务根域名
     *
     * @param request
     * @return
     */
    public static final String getServerRootDomain(HttpServletRequest request) {
        return getServerDomain(request).replaceAll(".*\\.(?=.*\\.)", "");
    }

    /**
     * 获取没有后缀的uri
     *
     * @param uri
     * @return
     */
    public static final String getURIWithoutSuffix(String uri) {
        if (uri == null || "".equals(uri.trim())) {
            return uri;
        }

        int pointIndex = uri.indexOf(".");
        if (pointIndex == -1) {
            return uri;
        }

        return uri.substring(0, pointIndex);
    }

    /**
     * 获取uri的后缀
     *
     * @param uri
     * @return
     */
    public static final String getURISuffix(String uri) {
        if (uri == null || "".equals(uri.trim())) {
            return uri;
        }

        int pointIndex = uri.indexOf(".");
        if (pointIndex == -1) {
            return "";
        }

        return uri.substring(pointIndex);
    }

    /**
     * 获取没有后缀的uri
     *
     * @param url
     * @param contextPath
     * @return
     */
    public static final String getURIWithoutSuffix(String url, String contextPath) {
        if (url == null || "".equals(url.trim())) {
            return url;
        }

        int contextPathPonit = url.indexOf(contextPath);

        if (contextPathPonit != -1) {
            url = url.substring(contextPathPonit);
        }

        int pointIndex = url.indexOf(".");

        if (pointIndex == -1) {
            return url;
        } else {
            return url.substring(0, pointIndex);
        }
    }

    public static String getURLWithoutContextPath(HttpServletRequest request){
        return getURLWithoutContextPath(request.getRequestURI(), request);
    }

    public static String getURLWithoutContextPath(String url, HttpServletRequest request){
        if (StringUtil.isNotBlank(url) && request != null && request.getContextPath().length() < url.length())
            return url.substring(request.getContextPath().length());
        return null;
    }

    /**
     * 是否是json请求
     *
     * @return
     */
    public static final boolean isAjaxUrl(HttpServletRequest request) {
        String accept = request.getHeader("Accept");

        if (StringUtil.isNotBlank(accept) && (accept.contains("application/json") || accept.contains("application/jsonp"))) {
            return true;
        }

        String uri = request.getRequestURI();
        int suffixIndex = uri.lastIndexOf(".");

        if (suffixIndex != -1) {
            String suffix = uri.substring(suffixIndex + 1);
            if ("json".equals(suffix) || "jsonp".equals(suffix)) {
                return true;
            }
        }

        String format = request.getParameter("format");

        if ("json".equals(format) || "jsonp".equals(format)) {
            return true;
        }

        String ajaxHeader = request.getHeader("X-Requested-With");

        if (StringUtil.isNotBlank(ajaxHeader) && "XMLHttpRequest".equalsIgnoreCase(ajaxHeader)) {
            return true;
        }

        return false;
    }

    /**
     * 是否是jsonp请求
     *
     * @return
     */
    public static final boolean isJsonp(HttpServletRequest request) {
        String accept = request.getHeader("Accept");

        if (StringUtil.isNotBlank(accept) && (accept.contains("application/jsonp"))) {
            return true;
        }

        String format = request.getParameter("format");

        if ("jsonp".equals(format)) {
            return true;
        }

        String uri = request.getRequestURI();
        int suffixIndex = uri.lastIndexOf(".");

        if (suffixIndex != -1) {
            String suffix = uri.substring(suffixIndex + 1);
            if ("jsonp".equals(suffix)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取用户访问地址
     *
     * @param request
     * @return
     */
    public static String getClientAddr(HttpServletRequest request) {
        String str = request.getHeader("X-Forwarded-For");

        if ((str == null) || (str.length() == 0) || ("unknown".equalsIgnoreCase(str))) {
            str = request.getHeader("X-Real-IP");
        }
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getHeader("Proxy-Client-IP");
        }
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getHeader("WL-Proxy-Client-IP");
        }
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getHeader("HTTP_CLIENT_IP");
        }
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getRemoteAddr();
        }

        if (str != null && str.indexOf(",") != -1) {
            str = str.substring(str.lastIndexOf(",") + 1, str.length()).trim();
        }

        return String.valueOf(str);
    }

    /**
     * 灰度处理url(当前环境如果是灰度环境则会在url上增加灰度标志gray，如果不是灰度环境则不修改url)
     *
     * @param url
     * @return
     */
    public static String grayUrl(String url) {
        if (url == null) {
            throw new SystemException("输入的url为空");
        }

        String grayTag = getGrayTag();

        if (StringUtil.isBlank(grayTag)) {
            return url;
        } else {
            String domain = getDomain(url);
            String http = getHttp(url);
            String www = getWWW(url);
            String uri = getURI(domain, url);

            if (!uri.equals(url)) {
                url = http + www + grayTag + domain + uri;
            }
        }

        return url;
    }

    public static boolean isGray() {
        String grayTag = System.getenv(ENVIRONMENT_GRAY);

        if (grayTag == null) {
            grayTag = System.getenv(ENVIRONMENT_GRAY.toLowerCase());
        }

        if (ENVIRONMENT_GRAY_VALUE.equalsIgnoreCase(grayTag)) {
            return true;
        } else {
            return false;
        }
    }

    private static String getDomain(String url) {
        String domain = null;
        Pattern p = Pattern.compile("(?<=http(s)?://)[^.].*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);

        if (matcher.find()) {
            domain = matcher.group();
        }

        if (domain == null) {
            domain = "";
        } else {
            String www = "www.";
            int index = domain.indexOf("www.");

            if (index != -1) {
                domain = domain.substring(index + www.length());
            }
        }

        return domain;
    }

    private static String getHttp(String url) {
        String http = "";

        if (url.indexOf("http://") != -1) {
            http = "http://";
        } else if (url.indexOf("https://") != -1) {
            http = "https://";
        }

        return http;
    }

    private static String getWWW(String url) {
        String www = "";

        if (url.indexOf("www.") != -1) {
            www = "www.";
        }

        return www;
    }

    private static String getURI(String domain, String url) {
        String uri = "";

        if (StringUtil.isNotBlank(domain)) {
            int index = url.indexOf(domain);

            if (index != -1) {
                uri = url.substring(index + domain.length());
            }
        } else {
            uri = url;
        }

        return uri;
    }

    private static String getGrayTag() {
        String grayTag = System.getenv(ENVIRONMENT_GRAY);

        if (grayTag == null) {
            grayTag = System.getenv(ENVIRONMENT_GRAY.toLowerCase());
        }

        return grayTag;
    }

    public static void main(String[] args) {
        String url = "http://payment.berbon.com/trade/callback/sztfBalancePay";
        String grayUrl = URLUtil.grayUrl(url);

        System.out.println(grayUrl);
    }

}
