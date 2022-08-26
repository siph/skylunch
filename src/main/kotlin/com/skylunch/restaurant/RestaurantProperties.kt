package com.skylunch.restaurant

import com.skylunch.restaurant.restaurantApi.RestaurantApiProperties
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
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
@ConstructorBinding
@ConfigurationProperties(prefix = "application.restaurant")
class RestaurantProperties(
    val api: RestaurantApiProperties,
    @Min(0)
    val daysUntilStale: Long,
    @Min(1)
    @Max(50_000)
    val radius: Long = 9260L,
)

