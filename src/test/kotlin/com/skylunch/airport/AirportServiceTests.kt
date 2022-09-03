package com.skylunch.airport

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.*
import com.skylunch.airport.airportApi.AirportApiService
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
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
        assertThat(
            airportService.getAirport(
                AirportCode(code = "LAX", codeType = getAirportCodeType("LAX"))
            ).block()
        ).isNotNull
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
            .getAirport(AirportCode(code = "lax", codeType = getAirportCodeType("lax"))).block()!!
        assertThat(updatedAirport.modified).isAfter(staleDate)
    }

    @Test
    fun `assert airport is not found`(){
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
