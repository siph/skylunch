package com.skylunch.airport.airportApi

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Data Transfer Object for the 'Api Ninjas Airport Api'.
 * This class omits several unused properties.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class AirportApiDTO(
    val icao: String?,
    val iata: String?,
    val name: String?,
    val latitude: String,
    val longitude: String,
)
