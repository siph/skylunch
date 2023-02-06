package com.skylunch.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories
class RedisConfiguration {

    @Bean
    fun jedisConnectionFactory(redisApplicationProperties: RedisApplicationProperties): JedisConnectionFactory {
        val redisConfiguration = RedisStandaloneConfiguration()
        redisConfiguration.hostName = redisApplicationProperties.hostName
        redisConfiguration.port = redisApplicationProperties.port
        redisConfiguration.username = redisApplicationProperties.username
        redisConfiguration.password = RedisPassword.of(redisApplicationProperties.password)
        return JedisConnectionFactory(redisConfiguration)
    }

    @Bean
    fun redisTemplate(connectionFactory: JedisConnectionFactory): RedisTemplate<*, *> {
        val template = RedisTemplate<ByteArray, ByteArray>()
        template.setConnectionFactory(connectionFactory)
        return template
    }
}
