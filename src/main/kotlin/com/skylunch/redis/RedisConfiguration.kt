package com.skylunch.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories
class RedisConfiguration {
    @Bean
    fun redisConnectionFactory(redisProperties: RedisProperties): LettuceConnectionFactory {
        return LettuceConnectionFactory(
            redisProperties.host,
            redisProperties.port,
        )
    }

    @Bean
    fun jedisConnectionFactory(redisProperties: RedisProperties): JedisConnectionFactory {
        val redisConfiguration = RedisStandaloneConfiguration()
        redisConfiguration.hostName = redisProperties.host
        redisConfiguration.port = redisProperties.port
        return JedisConnectionFactory(redisConfiguration)
    }

    @Bean
    fun redisTemplate(connectionFactory: LettuceConnectionFactory): RedisTemplate<*, *> {
        val template = RedisTemplate<ByteArray, ByteArray>()
        template.setConnectionFactory(connectionFactory);
        return template
    }
}
