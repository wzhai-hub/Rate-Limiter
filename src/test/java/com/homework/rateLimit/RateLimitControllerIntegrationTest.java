package com.homework.rateLimit.controller;

import com.homework.rateLimit.service.RedisRateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 下面测试依赖Redis，需要开启redis server

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RateLimitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisRateLimiterService redisRateLimiterService;

    private static final String API_ENDPOINT = "/api1";
    private static final String USER_ID = "user1";
    private static final String REDIS_KEY_PATTERN = "rate:" + USER_ID + ":*";

    @BeforeEach
    public void setup() {
        // 清除 Redis 中与测试相关的键
        redisTemplate.keys(REDIS_KEY_PATTERN).forEach(redisTemplate::delete);
    }

    @Test
    @Disabled
    public void testRateLimiterAllowsWithinLimit() throws Exception {
        int limit = 10; // 限流阈值

        for (int i = 0; i < limit; i++) {
            mockMvc.perform(get(API_ENDPOINT).param("userId", USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().string("API1: Request allowed"));
        }
    }

    @Test
    @Disabled
    public void testRateLimiterBlocksExcessRequests() throws Exception {
        int limit = 10; // 限流阈值

        // 发送超过限流阈值的请求
        for (int i = 0; i < limit; i++) {
            mockMvc.perform(get(API_ENDPOINT).param("userId", USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().string("API1: Request allowed"));
        }

        // 第 limit + 1 次请求应该被拒绝
        mockMvc.perform(get(API_ENDPOINT).param("userId", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("API1: Rate limit exceeded"));

        // 测试流量统计接口，确保统计数据符合预期
        mockMvc.perform(get("/stats").param("apiName", "api1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API: api1, Requests: " + (limit + 1) + ", Allowed: " + limit + ", Denied: 1"));
    }

    @Test
    @Disabled
    public void testConcurrentRequests() throws Exception {
        int limit = 10; // 限流阈值
        int threadCount = 100; // 并发线程数

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger allowedCount = new AtomicInteger();
        AtomicInteger rejectedCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    String response = mockMvc.perform(get(API_ENDPOINT).param("userId", USER_ID))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

                    if (response.contains("Request allowed")) {
                        allowedCount.incrementAndGet();
                    } else if (response.contains("Rate limit exceeded")) {
                        rejectedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);

        // 验证限流效果
        assertThat(allowedCount.get()).isEqualTo(limit);
        assertThat(rejectedCount.get()).isGreaterThan(0);
    }
}
