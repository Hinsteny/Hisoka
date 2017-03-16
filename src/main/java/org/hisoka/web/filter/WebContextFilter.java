package org.hisoka.web.filter;

import org.hisoka.common.util.other.LogUtil;
import org.hisoka.core.service.InitializeService;
import org.hisoka.web.context.WebContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class WebContextFilter implements Filter {

    public void init(FilterConfig arg0) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest heq = (HttpServletRequest) request;
            HttpServletResponse hsr = (HttpServletResponse) response;
            WebContext.registry(heq, hsr);
            filterChain.doFilter(request, response);
        } finally {
            WebContext.release();
            InitializeService.clearDynamicSources();
            LogUtil.clean();
        }
    }

    public void destroy() {

    }

}

