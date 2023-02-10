package com.skylunch.redis

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * Properties to define redis connection.
 */
@Validated
@ConfigurationProperties(prefix = "application.redis")
class RedisApplicationProperties(
    @field:URL(message = "Must resolve to a valid url")
    val hostName: String,
    @field:Min(value = 1, message = "Port can not be less than 1")
    @field:Max(value = 65535, message = "Port can not be more than 65535")
    val port: Int,
    @field:NotBlank(message = "Username can not be blank")
    val username: String,
    @field:NotBlank(message = "Password can not be blank")
    val password: String
)
