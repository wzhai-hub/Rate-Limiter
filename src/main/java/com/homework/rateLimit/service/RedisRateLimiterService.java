package com.homework.rateLimit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;

@Service
public class RedisRateLimiterService {

    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimiterService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String LUA_SCRIPT =
            "local key = KEYS[1] " +
                    "local maxTokens = tonumber(ARGV[1]) " +
                    "local refillRate = tonumber(ARGV[2]) " +
                    "local ttl = tonumber(ARGV[3]) " +
                    "local now = redis.call('TIME') " +
                    "local currentTime = tonumber(now[1]) * 1000 + math.floor(tonumber(now[2]) / 1000) " +
                    "local data = redis.call('HMGET', key, 'tokens', 'timestamp') " +
                    "local tokens = tonumber(data[1]) or maxTokens " +
                    "local lastRefill = tonumber(data[2]) or currentTime " +
                    "local elapsedTime = currentTime - lastRefill " +
                    "local newTokens = math.floor(elapsedTime * refillRate / 1000) " +
                    "tokens = math.min(tokens + newTokens, maxTokens) " +
                    "if tokens >= 1 then " +
                    "   redis.call('HMSET', key, 'tokens', tokens - 1, 'timestamp', currentTime) " +
                    "   redis.call('EXPIRE', key, ttl) " +
                    "   return 1 " +
                    "else " +
                    "   redis.call('HMSET', key, 'tokens', tokens, 'timestamp', lastRefill) " +
                    "   redis.call('EXPIRE', key, ttl) " +
                    "   return 0 " +
                    "end";

    public boolean isAllowed(String userId, String api, int maxTokens, int refillRate) {
        String key = String.format("rate:%s:%s", userId, api);
        int ttl = 60; // 键的过期时间，单位为秒

        // 执行 Lua 脚本
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(maxTokens),
                String.valueOf(refillRate),
                String.valueOf(ttl)
        );

        if (result != null && result == 1) {
            logger.debug("Rate limit passed for userId: {}, api: {}", userId, api);
            return true;
        } else {
            logger.debug("*warning* Rate limit exceeded for userId: {}, api: {}", userId, api);
            return false;
        }
    }
}
