package com.homework.rateLimit;

import com.homework.rateLimit.controller.RateLimitController;
import com.homework.rateLimit.service.RateLimiter;
import com.homework.rateLimit.service.TrafficStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RateLimitController.class)
public class RateLimitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateLimiter rateLimiter;

    @MockBean
    private TrafficStatsService trafficStatsService;

    @BeforeEach
    public void setup() {
        // 默认模拟允许请求
        when(rateLimiter.allowRequest(anyString(), anyString())).thenReturn(true);
    }

    @Test
    @Disabled
    public void testApi1RequestAllowed() throws Exception {
        // 模拟返回允许请求
        when(rateLimiter.allowRequest("user1", "api1")).thenReturn(true);
        when(trafficStatsService.getApiStats("api1")).thenReturn("API: api1, Requests: 5, Allowed: 3, Denied: 2");

        mockMvc.perform(get("/api1").param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API1: Request allowed"));

        // 验证流量统计服务是否调用
        verify(trafficStatsService).incrementStats("api1", true);
    }

    @Test
    public void testApi1RateLimitExceeded() throws Exception {
        // 模拟返回限流
        when(rateLimiter.allowRequest("user1", "api1")).thenReturn(false);

        mockMvc.perform(get("/api1").param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API1: Rate limit exceeded"));

        // 验证流量统计服务是否调用
        verify(trafficStatsService).incrementStats("api1", false);
    }

    @Test
    public void testApi2RequestAllowed() throws Exception {
        // 模拟返回允许请求
        when(rateLimiter.allowRequest("user1", "api2")).thenReturn(true);

        mockMvc.perform(get("/api2").param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API2: Request allowed"));

        // 验证流量统计服务是否调用
        verify(trafficStatsService).incrementStats("api2", true);
    }

    @Test
    public void testApi2RateLimitExceeded() throws Exception {
        // 模拟返回限流
        when(rateLimiter.allowRequest("user1", "api2")).thenReturn(false);

        mockMvc.perform(get("/api2").param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API2: Rate limit exceeded"));

        // 验证流量统计服务是否调用
        verify(trafficStatsService).incrementStats("api2", false);
    }

    @Test
    public void testApi3RequestAllowed() throws Exception {
        // 模拟返回允许请求
        when(rateLimiter.allowRequest("user1", "api3")).thenReturn(true);

        mockMvc.perform(get("/api3").param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API3: Request allowed"));

        // 验证流量统计服务是否调用
        verify(trafficStatsService).incrementStats("api3", true);
    }

    @Test
    public void testApi3RateLimitExceeded() throws Exception {
        // 模拟返回限流
        when(rateLimiter.allowRequest("user1", "api3")).thenReturn(false);

        mockMvc.perform(get("/api3").param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API3: Rate limit exceeded"));

        // 验证流量统计服务是否调用
        verify(trafficStatsService).incrementStats("api3", false);
    }

    @Test
    public void testGetStats() throws Exception {
        // 模拟流量统计数据返回
        when(trafficStatsService.getApiStats("api1")).thenReturn("API: api1, Requests: 5, Allowed: 3, Denied: 2");

        mockMvc.perform(get("/stats").param("apiName", "api1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API: api1, Requests: 5, Allowed: 3, Denied: 2"));
    }

    @Test
    public void testGetStatsWithInvalidApi() throws Exception {
        // 模拟返回无效的API名称
        when(trafficStatsService.getApiStats("invalidApi")).thenReturn("API not found");

        mockMvc.perform(get("/stats").param("apiName", "invalidApi"))
                .andExpect(status().isOk())
                .andExpect(content().string("API not found"));
    }

    @Test
    public void testApi3RequestStats() throws Exception {
        // 模拟API3请求被允许
        when(rateLimiter.allowRequest("user1", "api3")).thenReturn(true);
        when(trafficStatsService.getApiStats("api3")).thenReturn("API: api3, Requests: 10, Allowed: 8, Denied: 2");

        mockMvc.perform(get("/api3").param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API3: Request allowed"));

        // 验证流量统计服务是否调用
        verify(trafficStatsService).incrementStats("api3", true);
    }
}
