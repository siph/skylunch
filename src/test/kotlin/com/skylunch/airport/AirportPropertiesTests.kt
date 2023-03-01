package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiProperties
import io.kotest.common.runBlocking
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class AirportPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        runBlocking {
            checkAll(Arb.long(0, Long.MAX_VALUE)) { long ->
                val apiProperties = AirportApiProperties(baseUrl = "http://0.0.0.0", "key")
                val properties = AirportProperties(api = apiProperties, daysUntilStale = long)
                val errors = validator.validate(properties)
                Assertions.assertThat(errors.size).isEqualTo(0)
            }
        }
    }

    @Test
    fun `Assert nested invalid constraints fail`() {
        runBlocking {
            checkAll(Arb.long(Long.MIN_VALUE, -1)) { long ->
                val apiProperties = AirportApiProperties(baseUrl = "http://0.0.0.0", "key")
                val properties = AirportProperties(api = apiProperties, daysUntilStale = long)
                val errors = validator.validate(properties)
                Assertions.assertThat(errors.size).isGreaterThan(0)
            }
        }
    }
}
