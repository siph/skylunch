package com.skylunch.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories
class RedisConfiguration {

    @Bean
    fun jedisConnectionFactory(env: Environment): JedisConnectionFactory {
        val redisConfiguration = RedisStandaloneConfiguration()
        redisConfiguration.hostName = env.getRequiredProperty("spring.redis.host")
        redisConfiguration.port = env.getRequiredProperty("spring.redis.port").toInt()
        redisConfiguration.username = env.getRequiredProperty("spring.redis.username")
        redisConfiguration.password = RedisPassword.of(env.getRequiredProperty("spring.redis.password"))
        return JedisConnectionFactory(redisConfiguration)
    }

    @Bean
    fun redisTemplate(connectionFactory: JedisConnectionFactory): RedisTemplate<*, *> {
        val template = RedisTemplate<ByteArray, ByteArray>()
        template.setConnectionFactory(connectionFactory)
        return template
    }
}
