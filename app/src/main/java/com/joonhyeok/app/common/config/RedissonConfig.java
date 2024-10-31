package com.joonhyeok.app.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@EnableCaching
public class RedissonConfig {
//    @Value("${spring.data.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.data.redis.port}")
//    private int redisPort;
//
//    @Value("${spring.data.redis.prefix}")
//    private String prefix;

//    @Bean
//    public RedissonClient redissonClient() {
//        RedissonClient redisson = null;
//        Config config = new Config();
//        config.useSingleServer().setAddress(prefix + redisHost + ":" + redisPort);
//        redisson = Redisson.create(config);
//        return redisson;
//    }
}