package com.skylunch.restaurant.restaurantApi

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import org.springframework.validation.annotation.Validated

/**
 * Properties that are used by the restaurant api.
 * The [baseUrl] should point to google nearby search api or a test server.
 * The [apiKey] is provided by google and is injected into the request parameter.
 */
@Validated
class RestaurantApiProperties(
    @field:URL(message = "Must resolve to a valid url")
    val baseUrl: String,
    @field:NotBlank(message = "ApiKey can not be blank")
    val apiKey: String
)
