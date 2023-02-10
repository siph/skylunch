package com.skylunch.restaurant.restaurantApi

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class RestaurantApiPropertiesTests {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `Assert valid constraints pass`() {
        val properties = RestaurantApiProperties(baseUrl = "http://0.0.0.0", "key")
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(0)
    }

    @Test
    fun `Assert invalid constraints fail`() {
        val properties = RestaurantApiProperties(baseUrl = "localhost", "")
        val errors = validator.validate(properties)
        Assertions.assertThat(errors.size).isEqualTo(2)
    }
}
