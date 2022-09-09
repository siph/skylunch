package com.skylunch.restaurant.restaurantApi

import com.skylunch.restaurant.Restaurant
import com.skylunch.restaurant.RestaurantProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

/**
 * This class communicates with Google's Nearby Search Api.
 */
@Service
class RestaurantApiService(
    @Autowired private val webClientBuilder: WebClient.Builder,
    @Autowired private val restaurantProperties: RestaurantProperties,
) {
    companion object {
        val log = LoggerFactory.getLogger(RestaurantApiService::class.java)
    }
    private val webClient = webClientBuilder
        .baseUrl(
            UriComponentsBuilder
                .fromHttpUrl(restaurantProperties.api.baseUrl)
                .queryParam("key", restaurantProperties.api.apiKey)
                .build()
                .toUriString()
        )
        .build()

    /**
     * Returns a mono of [RestaurantApiDTO] which are within the radius set by[RestaurantProperties].
     */
    fun getRestaurants(location: Point): Mono<RestaurantApiDTO> {
        log.debug("Querying remote server for restaurants near location: {}", location)
        return webClient
            .get()
            .uri {
                it
                    .pathSegment("nearbysearch", "json")
                    .queryParam("type", "restaurant")
                    .queryParam("radius", restaurantProperties.radius)
                    .queryParam("location", "${location.y},${location.x}")
                    .build()
            }
            .retrieve()
            .bodyToMono(RestaurantApiDTO::class.java)
    }

    /**
     * Returns a [CandidatesDTO] which matches the [restaurant] address.
     */
    fun getRestaurant(restaurant: Restaurant): Mono<CandidatesDTO> {
        log.debug("Querying remote server for restaurant: {}", restaurant)
        val fields = "formatted_address,formatted_phone_number,geometry,name,rating,url,user_rating_total,website"
        return webClient
            .get()
            .uri {
                it
                    .pathSegment("findplacefromtext", "json")
                    .queryParam("fields", fields)
                    .queryParam("input", restaurant.address)
                    .queryParam("inputtype", "inputtype")
                    .build()
            }
            .retrieve()
            .bodyToMono(CandidatesDTO::class.java)
    }
}
