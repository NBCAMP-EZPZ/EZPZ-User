package com.sparta.ezpzuser.common.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.mode}")
    private String mode;

    @Value("${spring.data.redis.password}")
    private String password;

    private final RedisClusterProperties properties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        if (mode.equals("single")) {
            config.useSingleServer()
                    .setAddress("redis://127.0.0.1:6379")
                    .setPassword(password);
        } else if (mode.equals("cluster")) {
            System.out.println("---------------------------RedisConfig.redissonClient");
            ClusterServersConfig clusterServersConfig = config.useClusterServers().setPassword(password);
            properties.getNodes().forEach(clusterServersConfig::addNodeAddress);
//            config.useClusterServers().setPassword(password)
//                    .addNodeAddress("redis://127.0.0.1:6000")
//                    .addNodeAddress("redis://127.0.0.1:6001")
//                    .addNodeAddress("redis://127.0.0.1:6002")
//                    .addNodeAddress("redis://127.0.0.1:6003")
//                    .addNodeAddress("redis://127.0.0.1:6004")
//                    .addNodeAddress("redis://127.0.0.1:6005");
        } else {
            throw new IllegalArgumentException("Redisson mode not recognized");
        }

        return Redisson.create(config);
    }

}