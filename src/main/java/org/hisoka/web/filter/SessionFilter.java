package org.hisoka.web.filter;

import org.hisoka.common.util.http.URLUtil;
import org.hisoka.common.util.session.HttpSessionProxy;
import org.hisoka.core.session.apply.SessionManager;
import org.hisoka.core.session.config.SessionConfig;
import org.hisoka.core.session.context.SessionContext;
import org.hisoka.web.context.WebContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/2
 * @copyright: 2016 All rights reserved.
 */
public class SessionFilter extends OncePerRequestFilter {

    private SessionConfig sessionConfig;

    private SessionManager sessionManager;

    public void setSessionConfig(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final FilterChain filterChain)
            throws ServletException, IOException {
        if (!shouldFilter(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        HttpServletRequestWrapper httpServletRequestWrapper = new SessionHttpServletRequestWrapper(httpServletRequest, httpServletResponse);

        try {
            WebContext.registry(httpServletRequestWrapper, httpServletResponse);
            filterChain.doFilter(httpServletRequestWrapper, httpServletResponse);
        } finally {
            HttpSessionProxy httpSession = SessionContext.getSessionContext();
            sessionManager.saveSession(httpSession, sessionConfig);
            SessionContext.clean();
        }
    }

    public class SessionHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private HttpServletRequest httpServletRequest;
        private HttpServletResponse httpServletResponse;

        public SessionHttpServletRequestWrapper(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
            super(httpServletRequest);
            this.httpServletRequest = httpServletRequest;
            this.httpServletResponse = httpServletResponse;
        }

        @Override
        public HttpSession getSession(boolean create) {
            return sessionManager.getSession(httpServletRequest, httpServletResponse, create, sessionConfig);
        }

        @Override
        public HttpSession getSession() {
            return getSession(true);
        }
    }

    private boolean shouldFilter(HttpServletRequest request) {
        String currentURI = URLUtil.getURIWithoutSuffix(request.getRequestURI());
        String currentSuffix = URLUtil.getURISuffix(request.getRequestURI());
        String[] ignoreUris = sessionConfig.getIgnoreUris();
        String[] ignoreSuffixs = sessionConfig.getIgnoreSuffixs();

        if (ignoreUris != null && ignoreUris.length > 0) {
            for (int i = 0; i < ignoreUris.length; i++) {
                if (currentURI.equalsIgnoreCase(ignoreUris[i])) {
                    return false;
                }
            }
        }

        if (ignoreSuffixs != null && ignoreSuffixs.length > 0) {
            for (int i = 0; i < ignoreSuffixs.length; i++) {
                if (currentSuffix.equalsIgnoreCase(ignoreSuffixs[i])) {
                    return false;
                }
            }
        }

        return true;
    }

}
