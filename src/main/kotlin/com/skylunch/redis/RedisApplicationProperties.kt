package com.skylunch.redis

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * Properties to define redis connection.
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "application.redis")
class RedisApplicationProperties(
    val hostName: String,
    val port: Int,
    val username: String,
    val password: String
)
