package com.skylunch.airport

import com.fasterxml.jackson.databind.ObjectMapper
import com.skylunch.AbstractBaseDocumentTest
import com.skylunch.airport.airportApi.AirportApiDTO
import com.skylunch.airport.airportApi.AirportApiService
import com.skylunch.airportArb
import com.skylunch.getMockAirportProperties
import com.skylunch.getMockResponseOK
import io.kotest.common.runBlocking
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
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

    @Test
    fun `assert airport is found`() {
        runBlocking {
            checkAll(10, airportArb) { airport ->
                assertThat(airportRepository.findAll().size).isEqualTo(0)
                val result = airportRepository.save(airport)
                assertThat(result.iata).isEqualTo(airport.iata)
                assertThat(result.icao).isEqualTo(airport.icao)
                assertThat(result.name).isEqualTo(airport.name)
                assertThat(result.location).isEqualTo(airport.location)
                assertThat(
                    airportService.getAirport(
                        AirportCode(
                            code = airport.iata!!,
                            codeType = getAirportCodeType(airport.iata!!)
                        )
                    ).block()
                ).isNotNull
                airportRepository.deleteAll()
            }
        }
    }

    @Test
    fun `assert airport is updated`() {
        runBlocking {
            checkAll(10, airportArb) { airport ->
                assertThat(airportRepository.findAll().size).isEqualTo(0)
                airport.modified = LocalDateTime.now().minusDays(35L)
                airportRepository.save(airport)
                val airportApiDTO = AirportApiDTO(
                    icao = airport.icao,
                    iata = airport.iata,
                    name = airport.name,
                    longitude = airport.location.x.toString(),
                    latitude = airport.location.y.toString()
                )
                val json = ObjectMapper().writeValueAsString(airportApiDTO)
                val mockResponse = getMockResponseOK(json)
                server.enqueue(mockResponse)
                val updatedAirport = airportService.getAirport(
                    AirportCode(
                        code = airport.iata!!,
                        codeType = getAirportCodeType(airport.iata!!)
                    )
                ).block()!!
                assertThat(updatedAirport.modified).isAfter(airport.modified)
                assertThat(updatedAirport.location).isEqualTo(airport.location)
                assertThat(updatedAirport.name).isEqualTo(airport.name)
                assertThat(updatedAirport.id).isEqualTo(airport.id)
                airportRepository.deleteAll()
            }
        }
    }

    @Test
    fun `assert airport is saved`() {
        runBlocking {
            checkAll(10, airportArb) { airport ->
                assertThat(airportRepository.findAll().size).isEqualTo(0)
                val airportApiDTO = AirportApiDTO(
                    icao = airport.icao,
                    iata = airport.iata,
                    name = airport.name,
                    longitude = airport.location.x.toString(),
                    latitude = airport.location.y.toString()
                )
                val json = ObjectMapper().writeValueAsString(airportApiDTO)
                val mockResponse = getMockResponseOK(json)
                server.enqueue(mockResponse)
                airportService.getAirport(
                    AirportCode(
                        code = airport.iata!!,
                        codeType = getAirportCodeType(airport.iata!!)
                    )
                ).block()!!
                assertThat(airportRepository.findAll().size).isEqualTo(1)
                assertThat(airportRepository.findAll().first().icao).isEqualTo(airport.icao)
                airportRepository.deleteAll()
            }
        }
    }

    @Test
    fun `assert airport is not found`() {
        runBlocking {
            checkAll(10, airportArb) { airport ->
                assertThat(airportRepository.findAll().size).isEqualTo(0)
                airportRepository.save(airport)
                val json = "[]"
                val mockResponse = getMockResponseOK(json)
                server.enqueue(mockResponse)
                airportService.getAirport(
                    AirportCode(
                        code = airport.iata!!,
                        codeType = getAirportCodeType(airport.iata!!)
                    )
                ).block()!!
                // If `AAA` is randomly generated as the Arb value generate new unique value.
                var unique = "AAA"
                while (unique == airport.iata) {
                    unique = Arb.string(3, Codepoint.az()).toString()
                }
                assertThat(
                    airportService.getAirport(
                        AirportCode(
                            code = unique,
                            codeType = getAirportCodeType(unique)
                        )
                    ).blockOptional()
                ).isEmpty
                airportRepository.deleteAll()
            }
        }
    }
}
