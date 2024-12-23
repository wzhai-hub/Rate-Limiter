package com.homework.rateLimit;

import com.homework.rateLimit.config.RateLimiterConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@EnableConfigurationProperties(RateLimiterConfigProperties.class)
public class RateLimitApplication {
	public static void main(String[] args) {
		SpringApplication.run(RateLimitApplication.class, args);
	}
}
