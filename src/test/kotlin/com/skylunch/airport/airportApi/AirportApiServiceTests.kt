package com.skylunch.airport.airportApi

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.airport.AirportCode
import com.skylunch.airport.AirportProperties
import com.skylunch.airport.getAirportCodeType
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
        val properties = getMockProperties("${server.url("/v1/airports")}:${server.port}")
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
}

/**
 * Returns a mock [AirportProperties].
 * The default apiKey and daysUntilStale is 'key' and 0 respectively.
 * @param baseUrl the url that is injected into the [AirportApiService].
 * @return mock properties.
 */
fun getMockProperties(baseUrl: String): AirportProperties {
    return AirportProperties(
        api = AirportApiProperties(
            baseUrl = baseUrl,
            apiKey = "key",
        ),
        daysUntilStale = 0L
    )
}

/**
 * Returns a mock [AirportCode].
 * The default code is 'lax' and the [com.skylunch.airport.CodeType] property is dynamically generated.
 * @param code three or four digit airport code.
 * @return mock [AirportCode].
 */
fun getMockAirportCode(code: String = "lax"): AirportCode {
    return AirportCode(code, getAirportCodeType(code))
}

/**
 * Returns a mock [AirportApiDTO].
 * The default value is the actual returned value for 'lax'.
 * @return mock [AirportApiDTO].
 */
fun getMockAirportApiDTO(): AirportApiDTO {
    return AirportApiDTO(
        icao = "KLAX",
        iata = "LAX",
        name = "Los Angeles Internation Airport",
        latitude = "33.94250107",
        longitude = "-118.4079971",
    )
}
