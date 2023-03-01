package com.skylunch.security

import io.kotest.common.runBlocking
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SecurityPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        runBlocking {
            checkAll(
                Arb.string(3, 20, Codepoint.az()),
                Arb.string(3, 20, Codepoint.az())
            ) { key, value ->
                val properties = SecurityProperties(securityHeaderKey = key, securityHeaderValue = value)
                val errors = validator.validate(properties)
                Assertions.assertThat(errors.size).isEqualTo(0)
            }
        }
    }

    @Test
    fun `Assert nested invalid constraints fail`() {
        val properties = SecurityProperties(securityHeaderKey = "", securityHeaderValue = "")
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(2)
    }
}
