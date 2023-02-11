package com.skylunch.airport

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.AbstractBaseDocumentTest
import com.skylunch.airport.airportApi.AirportApiService
import com.skylunch.getMockAirport
import com.skylunch.getMockAirportApiDTO
import com.skylunch.getMockAirportProperties
import com.skylunch.getMockResponseOK
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AirportServiceTests : AbstractBaseDocumentTest() {

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
            daysUntilStale = 10L
        )
        val airportApiService = AirportApiService(WebClient.builder(), airportProperties)
        airportService = AirportService(
            airportApiService = airportApiService,
            airportRepository = airportRepository,
            airportProperties = airportProperties
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
    fun `assert airport is found`() {
        val airport = getMockAirport()
        airportRepository.save(airport)
        assertThat(
            airportService.getAirport(
                AirportCode(code = "LAX", codeType = getAirportCodeType("LAX"))
            ).block()
        ).isNotNull
    }

    @Test
    fun `assert airport is updated`() {
        val staleDate = LocalDateTime.now().minusDays(35L)
        val airport = getMockAirport(staleDate)
        airportRepository.save(airport)
        val json = ObjectMapper().writeValueAsString(getMockAirportApiDTO())
        val mockResponse = getMockResponseOK(json)
        server.enqueue(mockResponse)
        val updatedAirport = airportService
            .getAirport(AirportCode(code = "lax", codeType = getAirportCodeType("lax"))).block()!!
        assertThat(updatedAirport.modified).isAfter(staleDate)
    }

    @Test
    fun `assert airport is saved`() {
        val airportApiDTO = getMockAirportApiDTO()
        val json = ObjectMapper().writeValueAsString(airportApiDTO)
        val mockResponse = getMockResponseOK(json)
        server.enqueue(mockResponse)
        airportService.getAirport(AirportCode(code = "lax", codeType = getAirportCodeType("lax"))).block()!!
        assertThat(airportRepository.findAll().size).isEqualTo(1)
        assertThat(airportRepository.findAll().first().icao).isEqualTo("KLAX")
    }

    @Test
    fun `assert airport is not found`() {
        val airport = getMockAirport()
        airportRepository.save(airport)
        val json = "[]"
        val mockResponse = getMockResponseOK(json)
        server.enqueue(mockResponse)
        assertThat(
            airportService.getAirport(AirportCode(code = "dia", codeType = getAirportCodeType("dia"))).block()
        ).isNull()
    }
}
