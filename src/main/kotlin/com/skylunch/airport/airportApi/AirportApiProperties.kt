package com.skylunch.airport.airportApi

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import org.springframework.validation.annotation.Validated

/**
 * Properties that are used by [com.skylunch.airport.airportApi.AirportApiService].
 * The [baseUrl] should point to rapidApi or a test server.
 * The [apiKey] is provided by rapidApi and is injected into the headers.
 */
@Validated
data class AirportApiProperties(
    @field:URL(message = "Must resolve to a valid url")
    val baseUrl: String,
    @field:NotBlank(message = "ApiKey can not be blank")
    val apiKey: String
)
