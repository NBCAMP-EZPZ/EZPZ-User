package com.sparta.ezpzuser.common.config.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private final RedisClusterProperties properties;

    @Value("${spring.data.redis.mode}")
    private String mode;

    @Bean
    public RedissonClient redissonClient() {
        switch (mode) {
            case "standalone" -> {
                return Redisson.create();
            }
            case "cluster" -> {
                Config config = new Config();
                config.useClusterServers()
                        .setNodeAddresses(properties.getNodes());
                return Redisson.create(config);
            }
            default -> throw new IllegalArgumentException("Invalid redisson mode");
        }
    }

}