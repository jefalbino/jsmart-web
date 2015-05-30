package com.jsmart5.framework.filter;

import com.jsmart5.framework.manager.SmartContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AsyncFilter implements Filter {

    public static final String ENCODING = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // DO NOTHING
    }

    @Override
    public void destroy() {
        // DO NOTHING
    }

    // Filter used case AsyncContext is dispatched internally by AsyncBean implementation
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpRequest.setCharacterEncoding(ENCODING);
        httpResponse.setCharacterEncoding(ENCODING);

        // Initiate bean context based on current thread instance
        SmartContext.initCurrentInstance(httpRequest, httpResponse);

        Throwable throwable = null;
        try {
            filterChain.doFilter(httpRequest, httpResponse);
        } catch (Throwable thrown) {
            throwable = thrown;
            thrown.printStackTrace();
        }

        // Close bean context based on current thread instance
        SmartContext.closeCurrentInstance();

        // Case internal server error
        if (throwable != null) {
            if (throwable instanceof IOException) {
                throw new IOException(throwable);
            }
            throw new ServletException(throwable);
        }
    }

}
