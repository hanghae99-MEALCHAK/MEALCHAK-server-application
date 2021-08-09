package com.mealchak.mealchakserverapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

//@Profile("13.125.39.31")
@Profile("local")
@Configuration
public class EmbeddedRedisConfig {
    private RedisServer redisServer;

//    @PostConstruct
//    public void start() {
//        redisServer = new RedisServer(6379);
//        redisServer.start();
//    }

    @PreDestroy
    public void stop() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
