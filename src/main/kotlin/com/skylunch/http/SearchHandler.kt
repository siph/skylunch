package com.skylunch.http

import com.skylunch.airport.Airport
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Http handler to search for restaurants using [searchService].
 */
@Component
class SearchHandler(private val searchService: SearchService) {

    /**
     * Returns a [ServerResponse] that contains a list of [SearchDTO] in the searchable area surrounding an [Airport].
     * If no 'code' request parameter is found: a bad request is returned.
     */
    fun findRestaurantsByAirportCode(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest
            .queryParam("code")
            .map { code ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                        BodyInserters.fromProducer(
                            searchService.findRestaurantsByAirportCode(code).map { it.toSearchDTO() },
                            object : ParameterizedTypeReference<List<SearchDTO>>() {}
                        )
                    )
            }.orElseGet {
                ServerResponse
                    .badRequest()
                    .body(BodyInserters.fromValue("A code must be provided to search"))
            }
    }
}
