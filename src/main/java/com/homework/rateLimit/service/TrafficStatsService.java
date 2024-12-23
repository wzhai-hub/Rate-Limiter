package com.homework.rateLimit.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TrafficStatsService {

    // 存储每个 API 的统计信息
    private final ConcurrentHashMap<String, ApiStats> apiStatsMap = new ConcurrentHashMap<>();

    // 增加请求统计
    public void incrementStats(String apiName, boolean isAllowed) {
        ApiStats stats = apiStatsMap.computeIfAbsent(apiName, key -> new ApiStats());

        if (isAllowed) {
            stats.incrementAllowed();
        } else {
            stats.incrementDenied();
        }
    }

    // 获取指定 API 的统计信息
    public String getApiStats(String apiName) {
        ApiStats stats = apiStatsMap.get(apiName);
        if (stats != null) {
            return String.format("API: %s, Requests: %d, Allowed: %d, Denied: %d",
                    apiName, stats.getTotalRequests(), stats.getAllowedRequests(), stats.getDeniedRequests());
        }
        return "No stats available for API: " + apiName;
    }

    // 内部类，用于存储 API 统计数据
    private static class ApiStats {
        private final AtomicInteger totalRequests = new AtomicInteger(0);
        private final AtomicInteger allowedRequests = new AtomicInteger(0);
        private final AtomicInteger deniedRequests = new AtomicInteger(0);

        // 增加允许的请求
        public void incrementAllowed() {
            totalRequests.incrementAndGet();
            allowedRequests.incrementAndGet();
        }

        // 增加被拒绝的请求
        public void incrementDenied() {
            totalRequests.incrementAndGet();
            deniedRequests.incrementAndGet();
        }

        public int getTotalRequests() {
            return totalRequests.get();
        }

        public int getAllowedRequests() {
            return allowedRequests.get();
        }

        public int getDeniedRequests() {
            return deniedRequests.get();
        }
    }
}
