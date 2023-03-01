package com.skylunch.restaurant

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.AbstractBaseDocumentTest
import com.skylunch.getMockResponseOK
import com.skylunch.getMockRestaurantProperties
import com.skylunch.placeArb
import com.skylunch.restaurant.restaurantApi.CandidatesDTO
import com.skylunch.restaurant.restaurantApi.Geometry
import com.skylunch.restaurant.restaurantApi.LatLngLiteral
import com.skylunch.restaurant.restaurantApi.Place
import com.skylunch.restaurant.restaurantApi.RestaurantApiDTO
import com.skylunch.restaurant.restaurantApi.RestaurantApiService
import com.skylunch.restaurantArb
import io.kotest.common.runBlocking
import io.kotest.property.checkAll
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
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

    @Test
    fun `assert restaurant is found`() {
        runBlocking {
            checkAll(10, restaurantArb) { restaurant ->
                assertThat(restaurantRepository.findAll().size).isEqualTo(0)
                restaurant.modified = LocalDateTime.now()
                val location = restaurantRepository.save(restaurant).location
                val result = restaurantService.getRestaurants(location).blockFirst()!!
                assertThat(result.name).isEqualTo(restaurant.name)
                assertThat(result.address).isEqualTo(restaurant.address)
                assertThat(result.phoneNumber).isEqualTo(restaurant.phoneNumber)
                assertThat(result.url).isEqualTo(restaurant.url)
                assertThat(result.rating).isEqualTo(restaurant.rating)
                assertThat(result.totalRating).isEqualTo(restaurant.totalRating)
                assertThat(result.website).isEqualTo(restaurant.website)
                restaurantRepository.deleteAll()
            }
        }
    }

    @Test
    fun `assert restaurant is updated`() {
        runBlocking {
            checkAll(10, restaurantArb) { restaurant ->
                assertThat(restaurantRepository.findAll().size).isEqualTo(0)
                val staleDate = LocalDateTime.now().minusDays(35L)
                restaurant.modified = staleDate
                restaurantRepository.save(restaurant)
                val candidatesDTO = CandidatesDTO(
                    candidates = listOf(
                        Place(
                            address = restaurant.address,
                            phoneNumber = restaurant.phoneNumber,
                            rating = restaurant.rating,
                            totalRating = restaurant.totalRating,
                            url = restaurant.url,
                            website = restaurant.website,
                            name = "new: ${restaurant.name}",
                            geometry = Geometry(
                                location = LatLngLiteral(
                                    lat = restaurant.location.y,
                                    lng = restaurant.location.x
                                )
                            )
                        )
                    )
                )
                val json = ObjectMapper().writeValueAsString(candidatesDTO)
                val mockResponse = getMockResponseOK(json)
                server.enqueue(mockResponse)
                val updatedRestaurant = restaurantService.getRestaurants(restaurant.location)
                    .filter { it.id == restaurant.id }
                    .blockFirst()!!
                assertThat(updatedRestaurant.modified).isAfter(staleDate)
                assertThat(updatedRestaurant.name).isEqualTo("new: ${restaurant.name}")
                assertThat(updatedRestaurant.address).isEqualTo(restaurant.address)
                assertThat(updatedRestaurant.phoneNumber).isEqualTo(restaurant.phoneNumber)
                assertThat(updatedRestaurant.url).isEqualTo(restaurant.url)
                assertThat(updatedRestaurant.rating).isEqualTo(restaurant.rating)
                assertThat(updatedRestaurant.totalRating).isEqualTo(restaurant.totalRating)
                assertThat(updatedRestaurant.website).isEqualTo(restaurant.website)
                restaurantRepository.deleteAll()
            }
        }
    }

    @Test
    fun `assert new restaurants are cached`() {
        runBlocking {
            checkAll(10, placeArb) { place ->
                assertThat(restaurantRepository.findAll().size).isEqualTo(0)
                val json = ObjectMapper().writeValueAsString(RestaurantApiDTO(results = listOf(place), status = "OK"))
                val mockResponse = getMockResponseOK(json)
                server.enqueue(mockResponse)
                restaurantService.getRestaurants(
                    Point(
                        place.geometry.location.lat,
                        place.geometry.location.lng
                    )
                ).blockFirst()!!
                assertThat(restaurantRepository.findAll().size).isEqualTo(1)
                restaurantRepository.deleteAll()
            }
        }
    }
}
