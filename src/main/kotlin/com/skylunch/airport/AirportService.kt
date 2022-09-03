package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiDTO
import com.skylunch.airport.airportApi.AirportApiService
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import java.util.*

/**
 * Service layer to interact with the [airportRepository] and remote [airportApiService].
 */
@Service
class AirportService(
    private val airportApiService: AirportApiService,
    private val airportRepository: AirportRepository,
    private val airportProperties: AirportProperties,
) {

    /**
     * Will search the repository for an [Airport] document using the [airportCode]. If a document is
     * found the freshness of the document will be validated according to the [AirportProperties].
     * If either the document is not found or the document is stale, the remote server will be queried
     * for a new document.
     * @return a fresh airport.
     */
    fun getAirport(airportCode: AirportCode): Mono<Airport> {
        return findAirport(airportCode)
            .map {
                refreshAirport(it)
            }.orElseGet {
                airportApiService
                    .getAirport(airportCode)
                    .map { saveAirport(it) }
                    .toMono()
            }
    }

    private fun refreshAirport(airport: Airport): Mono<Airport> {
        if (airportProperties.daysUntilStale == 0L) { return airport.toMono() }
        val stalenessDate = LocalDateTime.now().minusDays(airportProperties.daysUntilStale)
        return when (airport.modified.isBefore(stalenessDate)) {
            true -> {
                airportApiService
                    .getAirport(airportToAirportCode(airport))
                    .map {
                        updateAirport(
                            Airport(
                                id = airport.id,
                                iata = it.iata,
                                icao = it.icao,
                                name = it.name,
                                location = getPoint(it),
                                modified = LocalDateTime.now(),
                            )
                        )
                    }.toMono()
            }
            false -> airport.toMono()
        }
    }

    private fun findAirport(airportCode: AirportCode): Optional<Airport> {
        return when (airportCode.codeType) {
            CodeType.IATA -> airportRepository.findByIataIgnoreCase(airportCode.code)
            CodeType.ICAO -> airportRepository.findByIcaoIgnoreCase(airportCode.code)
        }
    }

    private fun saveAirport(airportApiDTO: AirportApiDTO): Airport {
        return airportRepository.save(
            Airport(
                iata = airportApiDTO.iata,
                icao = airportApiDTO.icao,
                name = airportApiDTO.name,
                location = getPoint(airportApiDTO),
                modified = LocalDateTime.now(),
            )
        )
    }

    private fun updateAirport(airport: Airport): Airport {
        return airportRepository.save(airport)
    }

    private fun getPoint(airportApiDTO: AirportApiDTO): Point {
        return Point(airportApiDTO.longitude.toDouble(), airportApiDTO.latitude.toDouble())
    }
}
