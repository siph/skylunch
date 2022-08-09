package com.skylunch.skylunch.redis

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "application.redis")
data class RedisProperties(
    val host: String,
    val port: Int,
)
