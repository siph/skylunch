package com.skylunch.security

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SecurityPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        val properties = SecurityProperties(securityHeaderKey = "key", securityHeaderValue = "value")
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(0)
    }

    @Test
    fun `Assert nested invalid constraints fail`() {
        val properties = SecurityProperties(securityHeaderKey = "", securityHeaderValue = "")
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(2)
    }
}
