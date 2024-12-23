package com.homework.rateLimit.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RateLimiterConfigPropertiesTest {

    @Autowired
    private RateLimiterConfigProperties rateLimiterConfigProperties;

    @Test
    public void testConfigurationBinding() {
        assertThat(rateLimiterConfigProperties.getDefaultConfig().getLimitPerMinute()).isEqualTo(1000);

        assertThat(rateLimiterConfigProperties.getUsers()).isNotNull();
        assertThat(rateLimiterConfigProperties.getUsers().get("user1").get("api1")).isEqualTo(10);
        assertThat(rateLimiterConfigProperties.getUsers().get("user2").get("api2")).isEqualTo(6000);
    }
}
