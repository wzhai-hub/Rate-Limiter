package com.homework.rateLimit.service;

import com.homework.rateLimit.config.RateLimiterConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class RateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);
    @Autowired
    private RedisRateLimiterService redisRateLimiterService;

    @Autowired
    private RateLimiterConfigProperties configProperties;

    public int calculateRefillRate(int requestsPerMinute) {
        return (int) Math.ceil(requestsPerMinute / 60.0); // 默认向上取整
    }

    public boolean allowRequest(String userId, String apiName) {
        logger.debug("allowRequest userId: {}, apiName: {}", userId, apiName);

        // 获取用户配置或默认配置
        Map<String, Integer> userConfig = configProperties.getUsers().get(userId);
        if (userConfig != null) {
            logger.debug("Retrieved userConfig for userId {}: {}", userId, userConfig);
        } else {
            logger.warn("No userConfig found for userId {}", userId);
        }


        int limit = configProperties.getDefaultConfig().getLimitPerMinute();
        logger.debug("Default allowRequest limit: {}", limit);

        // 默认限流值
        if (userConfig != null && userConfig.containsKey(apiName)) {
            limit = userConfig.get(apiName);
            logger.debug("userConfig allowRequest limit: {}", limit);
        }

        // 计算每秒令牌速率
        int refillRate = calculateRefillRate(limit);
        logger.debug("Calculated refill rate: {}", refillRate);

        // 判断请求是否被允许
        return redisRateLimiterService.isAllowed(userId, apiName, limit, refillRate);
    }
}
