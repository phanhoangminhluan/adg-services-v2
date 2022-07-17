package com.adg.api.general.http;

import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.17 17:36
 */
@Component
public class CustomFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ContentCachingRequestWrapper contentCachingRequestWrapper = new ContentCachingRequestWrapper(
                (HttpServletRequest) servletRequest);

        filterChain.doFilter(contentCachingRequestWrapper, servletResponse);
    }
}
