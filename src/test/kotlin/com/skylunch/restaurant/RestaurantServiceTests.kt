package com.skylunch.restaurant

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.AbstractBaseDocumentTest
import com.skylunch.getMockCandidatesDTO
import com.skylunch.getMockResponseOK
import com.skylunch.getMockRestaurantProperties
import com.skylunch.restaurant.restaurantApi.Geometry
import com.skylunch.restaurant.restaurantApi.LatLngLiteral
import com.skylunch.restaurant.restaurantApi.Place
import com.skylunch.restaurant.restaurantApi.RestaurantApiDTO
import com.skylunch.restaurant.restaurantApi.RestaurantApiService
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.geo.Point
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestaurantServiceTests : AbstractBaseDocumentTest() {

    @Autowired
    lateinit var restaurantRepository: RestaurantRepository

    private lateinit var restaurantService: RestaurantService

    private lateinit var server: MockWebServer

    @BeforeAll
    fun setUp() {
        server = MockWebServer()
        server.start()
        val restaurantProperties = getMockRestaurantProperties(
            baseUrl = "${server.url("/maps/api/place")}",
            daysUntilStale = 10L
        )
        val restaurantApiService = RestaurantApiService(WebClient.builder(), restaurantProperties)
        restaurantService = RestaurantService(
            restaurantProperties = restaurantProperties,
            restaurantRepository = restaurantRepository,
            restaurantApiService = restaurantApiService
        )
    }

    @AfterAll
    fun tearDown() {
        server.shutdown()
    }

    @BeforeEach
    fun initialize() {
        restaurantRepository.deleteAll()
    }

    @Test
    fun `assert restaurant is found`() {
        val restaurant = getMockRestaurant()
        val location = restaurantRepository.save(restaurant).location
        val result = restaurantService.getRestaurants(location).toIterable()
        assertThat(result).isNotEmpty
    }

    @Test
    fun `assert restaurant is updated`() {
        val staleDate = LocalDateTime.now().minusDays(35L)
        val restaurant = restaurantRepository.save(getMockRestaurant(staleDate))
        val json = ObjectMapper().writeValueAsString(getMockCandidatesDTO())
        val mockResponse = getMockResponseOK(json)
        server.enqueue(mockResponse)
        val updatedRestaurant = restaurantService.getRestaurants(restaurant.location)
            .filter { it.id == restaurant.id }
            .blockFirst()!!
        assertThat(updatedRestaurant.modified).isAfter(staleDate)
    }

    @Test
    fun `assert new restaurants are cached`() {
        val place = Place(
            address = "address",
            phoneNumber = "1234",
            name = "spots",
            rating = "4",
            totalRating = "13",
            url = "b.c",
            website = "google.com",
            geometry = Geometry(LatLngLiteral(-118.4079971, 33.94250107))
        )
        val json = ObjectMapper().writeValueAsString(RestaurantApiDTO(results = listOf(place), status = "OK"))
        val mockResponse = getMockResponseOK(json)
        server.enqueue(mockResponse)
        restaurantService.getRestaurants(Point(-118.4079971, 33.94250107)).blockFirst()!!
        assertThat(restaurantRepository.findAll().size).isEqualTo(1)
    }

    private fun getMockRestaurant(
        modified: LocalDateTime = LocalDateTime.now(),
        location: Point = Point("-118.4079971".toDouble(), "33.94250107".toDouble())
    ): Restaurant {
        return Restaurant(
            id = "one",
            address = "address",
            phoneNumber = "1234",
            name = "spots",
            rating = "4",
            totalRating = "13",
            url = "b.c",
            website = "google.com",
            location = location,
            modified = modified
        )
    }
}
