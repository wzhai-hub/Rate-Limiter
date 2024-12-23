package com.homework.rateLimit;

import com.homework.rateLimit.controller.RateLimitController;
import com.homework.rateLimit.service.TrafficStatsService;
import com.homework.rateLimit.service.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)  // 使用 Mockito 扩展
class TrafficStatsServiceTest {

    private MockMvc mockMvc;

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private TrafficStatsService trafficStatsService;

    @InjectMocks
    private RateLimitController rateLimitController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rateLimitController).build();
    }

    @Test
    void testApi1Stats() throws Exception {
        // 模拟返回的统计数据
        when(trafficStatsService.getApiStats("api1"))
                .thenReturn("API: api1, Requests: 5, Allowed: 3, Denied: 2");

        // 测试流量统计接口
        mockMvc.perform(get("/stats").param("apiName", "api1"))
                .andExpect(status().isOk())
                .andExpect(content().string("API: api1, Requests: 5, Allowed: 3, Denied: 2"));
    }

    @Test
    void testApi2Stats() throws Exception {
        when(trafficStatsService.getApiStats("api2"))
                .thenReturn("API: api2, Requests: 3, Allowed: 2, Denied: 1");

        mockMvc.perform(get("/stats").param("apiName", "api2"))
                .andExpect(status().isOk())
                .andExpect(content().string("API: api2, Requests: 3, Allowed: 2, Denied: 1"));
    }
}
