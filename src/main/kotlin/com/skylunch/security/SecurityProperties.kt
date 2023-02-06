package com.skylunch.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * Properties for header based request filtering.
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "application.security")
class SecurityProperties(
    val securityHeaderKey: String,
    val securityHeaderValue: String
)
