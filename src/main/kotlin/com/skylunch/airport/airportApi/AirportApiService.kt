package com.skylunch.airport.airportApi

import com.skylunch.airport.AirportCode
import com.skylunch.airport.AirportProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

/**
 * This class communicates with the airport api hosted by rapidapi and developed by
 * api ninjas.
 */
@Service
class AirportApiService(
    @Autowired webClientBuilder: WebClient.Builder,
    @Autowired private val airportProperties: AirportProperties,
) {
    private val webClient = webClientBuilder
        .baseUrl(airportProperties.api.baseUrl)
        .defaultHeader("X-RapidAPI-Key", airportProperties.api.apiKey)
        .defaultHeader("X-RapidAPI-host", "airports-by-api-ninjas.p.rapidapi.com")
        .build()

    /**
     * Returns a mono of the list of [AirportApiDTO] that corresponds to the
     * provided [airportCode].
     */
    fun getAirport(airportCode: AirportCode): Mono<List<AirportApiDTO>> {
        return webClient
            .get()
            .uri { it.queryParam(airportCode.codeType.string, airportCode.code).build() }
            .retrieve()
            .bodyToFlux(AirportApiDTO::class.java)
            .collectList()
    }
}
