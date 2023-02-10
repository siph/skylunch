package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiProperties
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class AirportPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        val apiProperties = AirportApiProperties(baseUrl = "http://0.0.0.0", "key")
        val properties = AirportProperties(api = apiProperties, daysUntilStale = 14)
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(0)
    }

    @Test
    fun `Assert nested invalid constraints fail`() {
        val apiProperties = AirportApiProperties(baseUrl = "http://0.0.0.0", "key")
        val properties = AirportProperties(api = apiProperties, daysUntilStale = -1)
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(1)
    }
}
