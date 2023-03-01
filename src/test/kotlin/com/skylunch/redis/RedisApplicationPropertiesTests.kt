package com.skylunch.redis

import com.skylunch.URLarb
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

class RedisApplicationPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        runBlocking {
            checkAll(
                URLarb,
                Arb.string(3, 10, Codepoint.az()),
                Arb.string(3, 10, Codepoint.az())
            ) { url, username, password ->
                val properties = RedisApplicationProperties(
                    hostName = "${url.protocol}://${url.host}",
                    port = url.port,
                    username = username,
                    password = password
                )
                val errors = validator.validate(properties)
                Assertions.assertThat(errors.size).isEqualTo(0)
            }
        }
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
