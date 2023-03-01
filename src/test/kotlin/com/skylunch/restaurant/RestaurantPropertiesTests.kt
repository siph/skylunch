package com.skylunch.restaurant

import com.skylunch.restaurant.restaurantApi.RestaurantApiProperties
import io.kotest.common.runBlocking
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class RestaurantPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        runBlocking {
            checkAll(Arb.long(0, Long.MAX_VALUE)) {
                val apiProperties = RestaurantApiProperties(baseUrl = "http://0.0.0.0", "key")
                val properties = RestaurantProperties(api = apiProperties, daysUntilStale = it)
                val errors = validator.validate(properties)
                Assertions.assertThat(errors.size).isEqualTo(0)
            }
        }
    }

    @Test
    fun `Assert nested invalid constraints fail`() {
        val apiProperties = RestaurantApiProperties(baseUrl = "http://0.0.0.0", "key")
        val properties = RestaurantProperties(api = apiProperties, daysUntilStale = -1, radius = 50_001)
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(2)
    }
}
