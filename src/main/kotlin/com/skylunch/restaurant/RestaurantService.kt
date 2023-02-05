package com.skylunch.restaurant

import com.skylunch.restaurant.restaurantApi.Place
import com.skylunch.restaurant.restaurantApi.RestaurantApiDTO
import com.skylunch.restaurant.restaurantApi.RestaurantApiService
import org.slf4j.LoggerFactory
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.redis.domain.geo.Metrics
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

/**
 * Service layer to interact with the [restaurantRepository] and remote [restaurantApiService].
 */
@Service
class RestaurantService(
    private val restaurantProperties: RestaurantProperties,
    private val restaurantRepository: RestaurantRepository,
    private val restaurantApiService: RestaurantApiService
) {
    companion object {
        val log = LoggerFactory.getLogger(RestaurantService::class.java)
    }

    /**
     * Will search the repository for a collection of [Restaurant] documents using the [location]. If a
     * document is found the freshness of the document will be validated according to the [RestaurantProperties].
     * If either the document is not found or the document is stale, the remote server will be queried
     * for a new document.
     * @return a list of [Restaurant].
     */
    fun getRestaurants(location: Point): Flux<Restaurant> {
        log.debug("Query received for restaurants near location: {}", location)
        // Because Redis OM uses Jedis to interact with Redis instead of using Lettuce,
        // this repository call is blocking.
        val restaurants = restaurantRepository.findByLocationNear(
            location,
            Distance(restaurantProperties.radius.toDouble(), Metrics.METERS)
        )
        return when (restaurants.count()) {
            0 -> {
                restaurantApiService
                    .getRestaurants(location)
                    .map(this::saveRestaurants)
                    .flatMapMany { Flux.fromIterable(it) }
            }
            else -> {
                restaurants
                    .toFlux()
                    .flatMap(this::refreshRestaurant)
            }
        }
    }

    private fun refreshRestaurant(restaurant: Restaurant): Mono<Restaurant> {
        log.debug("Refreshing attempt on restaurant: {}", restaurant)
        val stalenessDate = LocalDateTime.now().minusDays(restaurantProperties.daysUntilStale)
        val isStale = restaurant.modified.isBefore(stalenessDate)
        if (restaurantProperties.daysUntilStale == 0L || !isStale) { return restaurant.toMono() }
        return restaurantApiService.getRestaurant(restaurant)
            .map { it.candidates.first() }
            .map { saveRestaurant(it, restaurant.id) }
    }

    private fun saveRestaurants(restaurantApiDTO: RestaurantApiDTO): Iterable<Restaurant> {
        log.trace("Saving restaurant from dto: {}", restaurantApiDTO)
        return restaurantApiDTO.results.map { saveRestaurant(it) }
    }

    private fun saveRestaurant(place: Place, id: String = ""): Restaurant {
        log.trace("Saving restaurant from place: {}", place)
        return restaurantRepository.save(
            Restaurant(
                id = id,
                address = place.address,
                phoneNumber = place.phoneNumber,
                name = place.name,
                rating = place.rating,
                totalRating = place.totalRating,
                url = place.url,
                website = place.website,
                location = Point(
                    place.geometry.location.lat,
                    place.geometry.location.lng
                )
            )
        )
    }
}
