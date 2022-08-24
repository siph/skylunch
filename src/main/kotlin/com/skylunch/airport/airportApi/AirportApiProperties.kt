package com.skylunch.airport.airportApi

import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
data class AirportApiProperties(
    val baseUrl: String,
    val apiKey: String,
)
