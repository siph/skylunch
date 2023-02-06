package com.skylunch.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * Properties to define redis connection.
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "application.security")
class SecurityProperties(
    val rapidApiHeaderValue: String
)
