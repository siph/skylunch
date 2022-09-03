package com.skylunch.airport.airportApi

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.getMockAirportApiDTO
import com.skylunch.getMockAirportCode
import com.skylunch.getMockAirportProperties
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AirportApiServiceTests {
    private lateinit var server: MockWebServer
    private lateinit var airportApiService: AirportApiService

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
        val properties = getMockAirportProperties("${server.url("/v1/airports")}:${server.port}")
        airportApiService = AirportApiService(WebClient.builder(), properties)
    }

    @Test
    fun `assert that remote call succeeds`() {
        val dto = getMockAirportApiDTO()
        val json = ObjectMapper().writeValueAsString(dto)
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(json)
            .setHeader("content-type", "application/json")
            .setHeader("content-length", json.length)
        val airportDto = airportApiService.getAirport(getMockAirportCode())
        server.enqueue(mockResponse)
        StepVerifier.create(airportDto)
            .expectNextMatches {
                assertThat(it).isEqualTo(dto)
                true
            }
            .verifyComplete()

        val request = server.takeRequest()
        Assertions.assertTrue(request.path!!.contains("iata=LAX"))
        Assertions.assertNotNull(request.getHeader("X-RapidAPI-Key"))
        Assertions.assertNotNull(request.getHeader("X-RapidAPI-host"))
    }
}
