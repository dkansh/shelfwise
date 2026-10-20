package com.shelfwise.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Very simple rate limiter: max N requests per minute per client IP.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, List<Long>> requestsByClient = new HashMap<>();

    @Value("${shelfwise.rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String client = getClientIp(request);
        long now = System.currentTimeMillis();

        List<Long> times = requestsByClient.computeIfAbsent(client, k -> new ArrayList<>());
        times.removeIf(t -> now - t > 60_000);

        if (times.size() >= requestsPerMinute) {
            response.setStatus(429);
            response.getWriter().write("Too many requests, please slow down");
            return false;
        }

        times.add(now);
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null) {
            return forwarded;
        }
        return request.getRemoteAddr();
    }
}
