package com.skylunch.airport.airportApi

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.airport.AirportCode
import com.skylunch.airport.getAirportCodeType
import com.skylunch.airportApiDTOArb
import com.skylunch.getMockAirportProperties
import io.kotest.common.runBlocking
import io.kotest.property.checkAll
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
    fun `assert that remote calls succeed`() {
        runBlocking {
            checkAll(10, airportApiDTOArb) { dto ->
                val json = ObjectMapper().writeValueAsString(dto)
                val mockResponse = MockResponse()
                    .setResponseCode(200)
                    .setBody(json)
                    .setHeader("content-type", "application/json")
                    .setHeader("content-length", json.length)
                val airportCode = AirportCode(dto.iata!!, getAirportCodeType(dto.iata!!))
                server.enqueue(mockResponse)
                val airportDTO = airportApiService.getAirport(airportCode)
                StepVerifier.create(airportDTO)
                    .expectNextMatches {
                        assertThat(it).isEqualTo(dto)
                        true
                    }
                    .verifyComplete()
                val request = server.takeRequest()
                Assertions.assertTrue(request.path!!.contains("iata=${dto.iata}"))
                Assertions.assertNotNull(request.getHeader("X-RapidAPI-Key"))
                Assertions.assertNotNull(request.getHeader("X-RapidAPI-host"))
            }
        }
    }
}
