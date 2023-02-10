package com.skylunch.redis

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class RedisApplicationPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        val properties = RedisApplicationProperties(
            hostName = "http://0.0.0.0",
            port = 1,
            username = "username",
            password = "password"
        )
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(0)
    }

    @Test
    fun `Assert nested invalid constraints fail`() {
        val properties = RedisApplicationProperties(
            hostName = "url",
            port = 65536,
            username = "",
            password = ""
        )
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(4)
    }
}
