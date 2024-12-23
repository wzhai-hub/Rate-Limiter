package com.homework.rateLimit.controller;

import com.homework.rateLimit.service.RateLimiter;
import com.homework.rateLimit.service.TrafficStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
public class RateLimitController {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitController.class);

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private TrafficStatsService trafficStatsService;

    @GetMapping("/api1")
    public String api1(@RequestParam String userId) {
        logger.debug("api1 request:");
        boolean isAllowed = rateLimiter.allowRequest(userId, "api1");
        if (!isAllowed) {
            trafficStatsService.incrementStats("api1", false);
            return "API1: Rate limit exceeded";
        }
        //some logic
        trafficStatsService.incrementStats("api1", true);
        return "API1: Request allowed";

    }

    @GetMapping("/api2")
    public String api2(@RequestParam String userId) {
        boolean isAllowed = rateLimiter.allowRequest(userId, "api2");
        if (!isAllowed) {
            trafficStatsService.incrementStats("api2", false);
            return "API2: Rate limit exceeded";
        }
        // some logic
        trafficStatsService.incrementStats("api2", true);
        return "API2: Request allowed";

    }

    @GetMapping("/api3")
    public String api3(@RequestParam String userId) {
        boolean isAllowed = rateLimiter.allowRequest(userId, "api3");
        if (!isAllowed) {
            trafficStatsService.incrementStats("api3", false);
            return "API3: Rate limit exceeded";
        }
        // some logic
        trafficStatsService.incrementStats("api3", true);
        return "API3: Request allowed";

    }

    // 获取 API 流量统计
    @GetMapping("/stats")
    public String getStats(@RequestParam String apiName) {
        return trafficStatsService.getApiStats(apiName);
    }
}
