package com.example.storemymeal.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ConcurrentHashMap<String, RequestRecord> requestCounts = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW_SIZE = Duration.ofSeconds(60);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        // Cleanup old windows
        requestCounts.entrySet().removeIf(entry -> currentTime - entry.getValue().windowStart > WINDOW_SIZE.toMillis());

        RequestRecord record = requestCounts.computeIfAbsent(clientIp, k -> new RequestRecord(currentTime, 1));

        synchronized (record) {
            if (currentTime - record.windowStart > WINDOW_SIZE.toMillis()) {
                record.windowStart = currentTime;
                record.count = 1;
                return true; // Allowed
            }
            if (record.count < MAX_REQUESTS) {
                record.count++;
                return true; // Allowed
            }
        }

        // If limit exceeded, respond with 429 Too Many Requests
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Whoops! Too many requests. Please wait a minute before analyzing another meal! 🌸\"}");
        return false; // Blocks request from reaching controller
    }

    private static class RequestRecord {
        long windowStart;
        int count;

        RequestRecord(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}