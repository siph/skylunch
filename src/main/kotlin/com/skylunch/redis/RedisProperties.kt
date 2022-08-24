package com.skylunch.redis

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * Properties that point to the [host] and [port] of a running redis instance.
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "application.redis")
data class RedisProperties(
    val host: String,
    val port: Int,
)
