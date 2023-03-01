package com.skylunch.restaurant.restaurantApi

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.getMockResponseOK
import com.skylunch.getMockRestaurantProperties
import com.skylunch.restaurant.RestaurantProperties
import com.skylunch.restaurantApiDTOArb
import io.kotest.common.runBlocking
import io.kotest.property.checkAll
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.geo.Point
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestaurantApiServiceTests {

    private lateinit var server: MockWebServer
    private lateinit var restaurantApiService: RestaurantApiService
    private lateinit var properties: RestaurantProperties

    @BeforeAll
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @AfterAll
    fun tearDown() {
        server.shutdown()
    }

    @BeforeEach
    fun initialize() {
        properties = getMockRestaurantProperties("${server.url("/v1/restaurants")}")
        restaurantApiService = RestaurantApiService(WebClient.builder(), properties)
    }

    @Test
    fun `assert that remote call succeeds`() {
        runBlocking {
            checkAll(10, restaurantApiDTOArb) { dto ->
                val first = dto.results.first()
                val json = ObjectMapper().writeValueAsString(dto)
                val mockResponse = getMockResponseOK(json)
                val restaurantDTO = restaurantApiService.getRestaurants(
                    Point(first.geometry.location.lng, first.geometry.location.lat)
                )
                server.enqueue(mockResponse)
                StepVerifier.create(restaurantDTO)
                    .expectNextMatches {
                        assertThat(it).isEqualTo(dto)
                        assertThat(ObjectMapper().writeValueAsString(it)).isEqualTo(json)
                        true
                    }
                    .verifyComplete()
                val request = server.takeRequest()
                assertThat(request.path).contains(
                    "location=${first.geometry.location.lat},${first.geometry.location.lng}"
                )
                assertThat(request.path).contains("radius=${properties.radius}")
                assertThat(request.path).contains("key=${properties.api.apiKey}")
            }
        }
    }
}
