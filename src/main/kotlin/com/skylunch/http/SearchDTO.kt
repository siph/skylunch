package com.skylunch.http

import com.skylunch.restaurant.Restaurant

data class SearchDTO(
    val name: String?,
    val address: String?,
    val phoneNumber: String?,
    val rating: String?,
    val url: String?,
    val totalRating: String?,
    val website: String?,
    val coords: Coords,
)

data class Coords(
    val latitude: String,
    val longitude: String,
)

fun Restaurant.toSearchDTO(): SearchDTO {
    return SearchDTO(
        name = this.name,
        address = this.address,
        phoneNumber = this.phoneNumber,
        rating = this.rating,
        url = this.url,
        totalRating = this.totalRating,
        website = this.website,
        coords = Coords(
            latitude = this.location.y.toString(),
            longitude = this.location.x.toString(),
        )
    )
}
