package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiService
import com.skylunch.airport.airportApi.getMockAirportCode
import com.skylunch.airport.airportApi.getMockAirportProperties
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.geo.Point
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AirportServiceTests {

    @Mock
    private lateinit var airportRepository: AirportRepository
    @Mock
    private lateinit var airportApiService: AirportApiService
    @Mock
    private lateinit var airportProperties: AirportProperties
    @InjectMocks
    private lateinit var airportService: AirportService

    private lateinit var server: MockWebServer

    @BeforeEach
    fun initialize() {
        airportProperties = getMockAirportProperties(
            baseUrl = "${server.url("/v1/airports")}",
            daysUntilStale = 10L,
        )
        airportApiService = AirportApiService(WebClient.builder(), airportProperties)
        airportRepository = Mockito.mock(AirportRepository::class.java)
        airportService = AirportService(airportApiService, airportRepository, airportProperties)
    }

//    @Test
    fun `assert airport is found`(){
        val airport = Airport(
            id = "1",
            icao = "KLAX",
            iata = "LAX",
            name = "Los Angeles International Airport",
            location = Point("-118.4079971".toDouble(), "33.94250107".toDouble()),
            modified = LocalDateTime.now().minusDays(100L)
        )
        given(airportRepository.save(airport)).willReturn(airport)
        given(airportRepository.findAirportByIata(airport.iata!!)).willReturn(Optional.of(airport))
        airportRepository.save(airport)
        val result: Optional<Airport> = airportService.getAirport(getMockAirportCode())
        assertTrue { result.isPresent }
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
