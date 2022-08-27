package com.skylunch.http

import com.skylunch.airport.Airport
import com.skylunch.airport.AirportCode
import com.skylunch.airport.AirportService
import com.skylunch.airport.getAirportCodeType
import com.skylunch.restaurant.Restaurant
import com.skylunch.restaurant.RestaurantService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Http handler to search for restaurants using [airportService] and [restaurantService].
 */
@Component
class SearchHandler(
    @Autowired private val airportService: AirportService,
    @Autowired private val restaurantService: RestaurantService,
) {

    /**
     * Returns a [ServerResponse] that contains a list of [Restaurant] in the searchable area surrounding an [Airport].
     * If no [Airport] can be found: a not found status is returned.
     * If no 'code' request parameter is found: a bad request is returned.
     */
    fun findRestaurantsByAirportCode(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest
            .queryParam("code")
            .map {code: String ->
                airportService.getAirport(AirportCode(code, getAirportCodeType(code)))
                    .map {airport: Airport ->
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                                BodyInserters.fromProducer(
                                    restaurantService.getRestaurants(airport.location),
                                    object: ParameterizedTypeReference<List<Restaurant>>() {}
                                )
                            )
                    }.orElseGet { ServerResponse.notFound().build() }
            }.orElseGet {
                ServerResponse
                    .badRequest()
                    .body(BodyInserters.fromValue("A code must be provided to search"))
            }
    }
}
