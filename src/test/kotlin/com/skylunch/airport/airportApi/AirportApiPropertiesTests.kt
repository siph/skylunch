package com.skylunch.airport.airportApi

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

class AirportApiPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        runBlocking {
            checkAll(URLarb, Arb.string(1, 25, Codepoint.az())) { url, key ->
                val properties = AirportApiProperties(
                    baseUrl = url.toString(),
                    key
                )
                val errors = validator.validate(properties)
                Assertions.assertThat(errors.size).isEqualTo(0)
            }
        }
    }

    @Test
    fun `Assert invalid constraints fail`() {
        val properties = AirportApiProperties(baseUrl = "localhost", "")
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(2)
    }
}
