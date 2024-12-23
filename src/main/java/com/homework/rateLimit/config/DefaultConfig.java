package com.homework.rateLimit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate-limiter.default")
public class DefaultConfig {
    private int limitPerMinute = 1000; // 设置默认的每分钟请求数限制

    // Getter 和 Setter
    public int getLimitPerMinute() {
        return limitPerMinute;
    }

    public void setLimitPerMinute(int limitPerMinute) {
        this.limitPerMinute = limitPerMinute;
    }
}