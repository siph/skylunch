package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiService
import com.skylunch.airport.airportApi.getMockProperties
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(MockitoExtension::class)
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
    fun setup() {

    }

    @BeforeEach
    fun initialize() {
        val properties = getMockProperties("${server.url("/v1/airports")}:${server.port}")
        airportApiService = AirportApiService(WebClient.builder(), properties)
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
