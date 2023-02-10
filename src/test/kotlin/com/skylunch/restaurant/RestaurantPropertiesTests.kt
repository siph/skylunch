package com.skylunch.restaurant

import com.skylunch.restaurant.restaurantApi.RestaurantApiProperties
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class RestaurantPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        val apiProperties = RestaurantApiProperties(baseUrl = "http://0.0.0.0", "key")
        val properties = RestaurantProperties(api = apiProperties, daysUntilStale = 14)
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(0)
    }

    @Test
    fun `Assert nested invalid constraints fail`() {
        val apiProperties = RestaurantApiProperties(baseUrl = "http://0.0.0.0", "key")
        val properties = RestaurantProperties(api = apiProperties, daysUntilStale = -1, radius = 50_001)
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(2)
    }
}
