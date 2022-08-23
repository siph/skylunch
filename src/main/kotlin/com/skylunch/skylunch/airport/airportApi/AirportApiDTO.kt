package com.skylunch.skylunch.airport.airportApi

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AirportApiDTO(
    val icao: String?,
    val iata: String?,
    val name: String?,
    val latitude: String,
    val longitude: String,
)
