package com.skylunch.restaurant

import com.skylunch.restaurant.restaurantApi.RestaurantApiService
import com.skylunch.restaurant.restaurantApi.getMockRestaurantProperties
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.redis.domain.geo.Metrics
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestaurantServiceTests {

    @Mock
    private lateinit var restaurantRepository: RestaurantRepository
    @Mock
    private lateinit var restaurantApiService: RestaurantApiService
    @Mock
    private lateinit var restaurantProperties: RestaurantProperties
    @InjectMocks
    private lateinit var restaurantService: RestaurantService

    private lateinit var server: MockWebServer

    @BeforeEach
    fun initialize() {
        restaurantProperties = getMockRestaurantProperties(
            baseUrl = "${server.url("/v1/airports")}:${server.port}",
            daysUntilStale = 10L,
        )
        restaurantApiService = RestaurantApiService(WebClient.builder(), restaurantProperties)
        restaurantRepository = Mockito.mock(RestaurantRepository::class.java)
        restaurantService = RestaurantService(restaurantProperties, restaurantRepository, restaurantApiService)
    }

    @Test
    fun `assert airport is found`(){
        val location = Point("-118.4079971".toDouble(), "33.94250107".toDouble())
        val restaurant = Restaurant(
            id = "one",
            address = "address",
            phoneNumber = "1234",
            name = "spots",
            rating = "4",
            totalRating = "13",
            url = "b.c",
            website = "google.com",
            location = location,
            modified = LocalDateTime.now(),
        )
        BDDMockito.given(restaurantRepository.save(restaurant)).willReturn(restaurant)
        BDDMockito
            .given(
                restaurantRepository
                    .findByLocationNear(
                        location,
                        Distance(
                            restaurantProperties.radius.toDouble(),
                            Metrics.METERS)
                    )
            ).willReturn(setOf(restaurant))
        restaurantRepository.save(restaurant)
        val result = restaurantService.getRestaurants(location).toIterable()
        assertThat(result).isNotEmpty
    }

    @BeforeAll
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @AfterAll
    fun tearDown() {
        server.shutdown()
    }
}
