package com.skylunch.restaurant.restaurantApi

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.skylunch.restaurant.Restaurant

/**
 * Data transfer object for the Google nearby search api.
 * Contains a return [status] and [results] of [Place].
 * Many unused return properties have been omitted.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class RestaurantApiDTO(
    @get:JsonProperty("results")
    val results: List<Place>,
    @get:JsonProperty("status")
    val status: String?,
)

/**
 * Data transfer object for the Google nearby search api.
 * Contains the properties needed to populate a [Restaurant] document.
 * Skylunch only handles restaurants so a [Place] can be assumed to be a restaurant.
 * Many unused return properties have been omitted.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Place(
    @get:JsonProperty("formatted_address")
    val address: String?,
    @get:JsonProperty("formatted_phone_number")
    val phoneNumber: String?,
    @get:JsonProperty("geometry")
    val geometry: Geometry,
    @get:JsonProperty("name")
    val name: String?,
    @get:JsonProperty("rating")
    val rating: String?,
    @get:JsonProperty("url")
    val url: String?,
    @get:JsonProperty("user_ratings_total")
    val totalRating: String?,
    @get:JsonProperty("website")
    val website: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Geometry(
    @get:JsonProperty("location")
    val location: LatLngLiteral,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LatLngLiteral(
    @get:JsonProperty("lat")
    val lat: Double,
    @get:JsonProperty("lng")
    val lng: Double,
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class CandidatesDTO(
    @get:JsonProperty("candidates")
    val candidates: List<Place>,
)
