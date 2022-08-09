package com.skylunch.skylunch.redis

import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


@TestConfiguration
class TestRedisConfiguration(redisProperties: RedisProperties) {
    private val redisServer: RedisServer

    init {
        redisServer = RedisServer(redisProperties.port)
    }

    @PostConstruct
    fun postConstruct() {
        redisServer.start()
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
    }
}
