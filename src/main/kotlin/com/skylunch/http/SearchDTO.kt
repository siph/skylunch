package com.skylunch.http

import com.skylunch.restaurant.Restaurant
import org.springframework.data.geo.Point

data class SearchDTO(
    val name: String?,
    val address: String?,
    val phoneNumber: String?,
    val rating: String?,
    val url: String?,
    val totalRating: String?,
    val website: String?,
    val coords: Coords
)

data class Coords(
    val latitude: String,
    val longitude: String
)

fun Point.toCoords(): Coords {
    return Coords(
        latitude = this.y.toString(),
        longitude = this.x.toString()
    )
}

fun Restaurant.toSearchDTO(): SearchDTO {
    return SearchDTO(
        name = this.name,
        address = this.address,
        phoneNumber = this.phoneNumber,
        rating = this.rating,
        url = this.url,
        totalRating = this.totalRating,
        website = this.website,
        coords = this.location.toCoords()
    )
}
