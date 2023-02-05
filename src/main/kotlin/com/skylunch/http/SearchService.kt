package com.skylunch.http

import com.skylunch.airport.AirportCode
import com.skylunch.airport.AirportService
import com.skylunch.airport.getAirportCodeType
import com.skylunch.restaurant.Restaurant
import com.skylunch.restaurant.RestaurantService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

@Service
class SearchService(
    private val airportService: AirportService,
    private val restaurantService: RestaurantService
) {
    fun findRestaurantsByAirportCode(code: String): Flux<Restaurant> {
        return airportService.getAirport(AirportCode(code, getAirportCodeType(code)))
            .toFlux()
            .flatMap { restaurantService.getRestaurants(it.location) }
    }
}
