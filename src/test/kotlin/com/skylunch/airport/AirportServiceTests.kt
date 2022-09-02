package com.skylunch.airport

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.AbstractBaseDocumentTest
import com.skylunch.airport.airportApi.AirportApiService
import com.skylunch.airport.airportApi.getMockAirportApiDTO
import com.skylunch.airport.airportApi.getMockAirportProperties
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.geo.Point
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AirportServiceTests: AbstractBaseDocumentTest() {

    @Autowired
    lateinit var airportRepository: AirportRepository

    private lateinit var airportService: AirportService

    private lateinit var server: MockWebServer

    @BeforeAll
    fun setUp() {
        server = MockWebServer()
        server.start()
        val airportProperties = getMockAirportProperties(
            baseUrl = "${server.url("/v1/airports")}",
            daysUntilStale = 10L,
        )
        val airportApiService = AirportApiService(WebClient.builder(), airportProperties)
        airportService = AirportService(
            airportApiService = airportApiService,
            airportRepository = airportRepository,
            airportProperties = airportProperties,
        )
    }

    @AfterAll
    fun tearDown() {
        server.shutdown()
    }

    @BeforeEach
    fun initialize() {
        airportRepository.deleteAll()
    }

    @Test
    fun `assert airport is found`(){
        val airport = getMockAirport()
        airportRepository.save(airport)
        assertThat(airportService.getAirport(AirportCode(code = "LAX", codeType = getAirportCodeType("LAX"))).isPresent)
            .isTrue
    }

    @Test
    fun `assert airport is updated`(){
        val staleDate = LocalDateTime.now().minusDays(35L)
        val airport = getMockAirport(staleDate)
        airportRepository.save(airport)
        val json = ObjectMapper().writeValueAsString(getMockAirportApiDTO())
        val mockResponse = getMockResponseOK(json)
        server.enqueue(mockResponse)
        val updatedAirport = airportService
            .getAirport(AirportCode(code = "lax", codeType = getAirportCodeType("lax"))).get()
        assertThat(updatedAirport.modified).isAfter(staleDate)
    }
    @Test
    fun `assert airport is not found`(){
        val airport = getMockAirport()
        airportRepository.save(airport)
        val json = "[]"
        val mockResponse = getMockResponseOK(json)
        server.enqueue(mockResponse)
        val maybeAirport = airportService
            .getAirport(AirportCode(code = "dia", codeType = getAirportCodeType("dia")))
        assertThat(maybeAirport).isEmpty
    }

    private fun getMockAirport(modified: LocalDateTime = LocalDateTime.now()): Airport {
        return Airport(
            id = "1",
            icao = "KLAX",
            iata = "LAX",
            name = "Los Angeles International Airport",
            location = Point("-118.4079971".toDouble(), "33.94250107".toDouble()),
            modified = modified
        )
    }

    private fun getMockResponseOK(body: String): MockResponse {
        return MockResponse()
            .setResponseCode(200)
            .setBody(body)
            .setHeader("content-type", "application/json")
            .setHeader("content-length", body.length)
    }
}
