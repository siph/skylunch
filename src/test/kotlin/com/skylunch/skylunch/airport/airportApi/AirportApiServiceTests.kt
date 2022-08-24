package com.skylunch.skylunch.airport.airportApi

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.skylunch.airport.AirportApiService
import com.skylunch.skylunch.airport.AirportCode
import com.skylunch.skylunch.airport.AirportProperties
import com.skylunch.skylunch.airport.getAirportCodeType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
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
        val properties = getProperties("${server.url("/v1/airports")}:${server.port}")
        airportApiService = AirportApiService(WebClient.builder(), properties)
    }

    @Test
    fun `assert that remote call succeeds`() {
        val dtos: MutableList<AirportApiDTO> = arrayListOf(getMockAirportApiDTO())
        val json = ObjectMapper().writeValueAsString(dtos)
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(json)
            .setHeader("content-type", "application/json")
            .setHeader("content-length", json.length)
        val airportDto = airportApiService.getAirport(getMockAirportCode())
        server.enqueue(mockResponse)
        StepVerifier.create(airportDto)
            .expectNextMatches {it.equals(dtos)}
            .verifyComplete()

        val request = server.takeRequest()
        Assertions.assertTrue(request.path!!.contains("iata=lax"))
        Assertions.assertNotNull(request.getHeader("X-RapidAPI-Key"))
        Assertions.assertNotNull(request.getHeader("X-RapidAPI-host"))
    }

    private fun getProperties(baseUrl: String): AirportProperties {
        return AirportProperties(
            api = AirportApiProperties(
                baseUrl = baseUrl,
                apiKey = "key",
            ),
            daysUntilStale = 0L
        )
    }

    private fun getMockAirportCode(code: String = "lax"): AirportCode {
        return AirportCode(code, getAirportCodeType(code))
    }

    private fun getMockAirportApiDTO(): AirportApiDTO {
        return AirportApiDTO(
            icao = "KLAX",
            iata = "LAX",
            name = "Los Angeles Internation Airport",
            latitude = "33.94250107",
            longitude = "-118.4079971",
        )
    }
}
