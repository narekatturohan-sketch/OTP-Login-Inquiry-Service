package com.otp.inquiry_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class HealthController {

    private final DataSource dataSource;
    private final StringRedisTemplate redisTemplate;

    public HealthController(
            DataSource dataSource,
            StringRedisTemplate redisTemplate
    ) {
        this.dataSource = dataSource;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/health")
    public String health() {

        try (Connection connection = dataSource.getConnection()) {

            String oracle = connection.isValid(2)
                    ? "Oracle: UP"
                    : "Oracle: DOWN";

            redisTemplate.opsForValue().set(
                    "health:test",
                    "OK"
            );

            String redis = redisTemplate.opsForValue()
                    .get("health:test");

            return oracle + " | Redis: " +
                    ("OK".equals(redis) ? "UP" : "DOWN");

        } catch (Exception e) {
            return "Oracle/Redis connection failed: "
                    + e.getMessage();
        }
    }
}
