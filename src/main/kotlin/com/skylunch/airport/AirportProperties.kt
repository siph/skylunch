package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiProperties
import jakarta.validation.constraints.Min
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "application.airport")
class AirportProperties(
    val api: AirportApiProperties,
    @Min(0)
    val daysUntilStale: Long,
)
