package com.skylunch.restaurant.restaurantApi

/**
 * Properties that are used by the restaurant api.
 * The [baseUrl] should point to google nearby search api or a test server.
 * The [apiKey] is provided by google and is injected into the request parameter.
 */
class RestaurantApiProperties(
    val baseUrl: String,
    val apiKey: String,
)
