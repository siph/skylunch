package com.skylunch.security

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * Properties for header based request filtering.
 */
@Validated
@ConfigurationProperties(prefix = "application.security")
class SecurityProperties(
    @field:NotBlank(message = "Can not be blank")
    val securityHeaderKey: String,
    @field:NotBlank(message = "Can not be blank")
    val securityHeaderValue: String
)
