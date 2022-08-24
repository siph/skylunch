package com.skylunch.airport.airportApi

import org.springframework.boot.context.properties.ConstructorBinding

/**
 * Properties that are used by [com.skylunch.airport.airportApi.AirportApiService].
 * The [baseUrl] should point to rapidApi or a test server.
 * The [apiKey] is provided by rapidApi and is injected into the headers.
 */
@ConstructorBinding
data class AirportApiProperties(
    val baseUrl: String,
    val apiKey: String,
)
