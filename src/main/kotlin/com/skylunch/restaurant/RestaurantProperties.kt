package com.skylunch.restaurant

import com.skylunch.restaurant.restaurantApi.RestaurantApiProperties
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * Properties that are used throughout the restaurant module.
 *
 * [api] contains the remote api service properties.
 * [daysUntilStale] limits how old a query must be to refresh from remote service.
 * A value of 0 means the queries never expire.
 * [radius] tells the search how far in meters to look for restaurants from the search point.
 * The default radius is five nautical miles.
 */
@Validated
@ConfigurationProperties(prefix = "application.restaurant")
class RestaurantProperties(
    @Valid
    val api: RestaurantApiProperties,
    @field:Min(value = 0, message = "Can not be less than 0")
    val daysUntilStale: Long,
    @field:Min(value = 1, message = "Can not be less than 0")
    @field:Max(value = 50_000, message = "Can not be more than 50,000")
    val radius: Long = 9260L
)
