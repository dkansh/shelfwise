package com.shelfwise.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    private static final String START_TIME = "shelfwise.startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());

        String query = request.getQueryString();
        log.debug("--> {} {}{} auth={}", request.getMethod(), request.getRequestURI(),
                query != null ? "?" + query : "", request.getHeader("Authorization"));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        Long start = (Long) request.getAttribute(START_TIME);
        long took = start != null ? System.currentTimeMillis() - start : -1;
        log.info("<-- {} {} {} ({} ms)", request.getMethod(), request.getRequestURI(), response.getStatus(), took);
    }
}
