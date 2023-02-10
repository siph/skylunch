package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiProperties
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * Properties that are used throughout the airport module.
 *
 * [api] contains the remote api service properties.
 * [daysUntilStale] limits how old a query must be to refresh from remote service.
 * A value of 0 means the queries never expire.
 */
@Validated
@ConfigurationProperties(prefix = "application.airport")
class AirportProperties(
    @Valid
    val api: AirportApiProperties,
    @field:Min(value = 0, message = "Can not be less than 0")
    val daysUntilStale: Long
)
