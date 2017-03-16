package org.hisoka.web.handler;

import org.hisoka.common.constance.Result;
import org.hisoka.common.util.http.URLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/1
 * @copyright: 2016 All rights reserved.
 */
public class CommonReturnValueHandler implements HandlerMethodReturnValueHandler {
    /**
     * 日志
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return Result.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws Exception {
        if (returnValue != null) {
            try {
                Result result = (Result) returnValue;
                HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
                HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
                response.setCharacterEncoding("UTF-8");
                StringBuffer responseSb = new StringBuffer();
                String resultStr = Result.toJSONString(result);

                if (URLUtil.isAjaxUrl(request)) {
                    if (URLUtil.isJsonp(request)) {
                        response.setContentType("application/javascript;charset=UTF-8");
                        String callback = request.getParameter("callback");
                        responseSb.append(callback).append("(").append(resultStr).append(")");
                        request.setAttribute("callback", callback);
                    } else {
                        response.setContentType("application/json;charset=UTF-8");
                        responseSb.append(resultStr);
                    }
                } else {
                    response.setContentType("text/html;charset=UTF-8");
                    responseSb.append(resultStr);
                }

                request.setAttribute("result", result);
                String responseStr = responseSb.toString();
                PrintWriter writer = null;

                try {
                    writer = response.getWriter();
                } catch (Exception e) {
                }

                if (writer != null) {
                    writer.write(responseStr);
                }
            } catch (Exception e) {
                log.error("Response write exception", e);
            }

            // 表明该请求已经处理，后面spring不会再处理
            mavContainer.setRequestHandled(true);
        }
    }
}
