package com.homework.rateLimit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterConfigProperties {

    private DefaultConfig defaultConfig; // 默认限流策略
    private Map<String, Map<String, Integer>> users; // 用户特定的限流配置

    // Getter 和 Setter
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Autowired
    public void setDefaultConfig(DefaultConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Map<String, Map<String, Integer>> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Map<String, Integer>> users) {
        this.users = users;
    }

    @PostConstruct
    public void init() {
        Logger logger = LoggerFactory.getLogger(RateLimiterConfigProperties.class);

        // 打印默认配置和用户特定的限流策略
        if (defaultConfig != null) {
            logger.info("Default Config - limitPerMinute: {}", defaultConfig.getLimitPerMinute());
        } else {
            logger.warn("Default Config is null");
        }
        logger.info("Users: {}", users);
    }
}