package com.skylunch.skylunch.airport

import com.skylunch.skylunch.airport.airportApi.AirportApiDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

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

    fun getAirport(airportCode: AirportCode): Mono<AirportApiDTO> {
        return webClient
            .get()
            .uri { it.queryParam(airportCode.codeType.string, airportCode.code).build() }
            .retrieve()
            .bodyToMono(AirportApiDTO::class.java)
    }
}
