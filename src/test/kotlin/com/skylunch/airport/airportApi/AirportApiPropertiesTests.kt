package com.skylunch.airport.airportApi

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class AirportApiPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        val properties = AirportApiProperties(baseUrl = "http://0.0.0.0", "key")
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(0)
    }

    @Test
    fun `Assert invalid constraints fail`() {
        val properties = AirportApiProperties(baseUrl = "localhost", "")
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(2)
    }
}
