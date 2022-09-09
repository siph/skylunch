package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiDTO
import com.skylunch.airport.airportApi.AirportApiService
import org.slf4j.LoggerFactory
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
    companion object {
        val log = LoggerFactory.getLogger(AirportService::class.java)
    }

    /**
     * Will search the repository for an [Airport] document using the [airportCode]. If a document is
     * found the freshness of the document will be validated according to the [AirportProperties].
     * If either the document is not found or the document is stale, the remote server will be queried
     * for a new document.
     * @return a fresh airport.
     */
    fun getAirport(airportCode: AirportCode): Mono<Airport> {
        log.debug("Query received for airport with code: {}", airportCode)
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
        log.debug("Refreshing attempt on airport: {}", airport)
        if (airportProperties.daysUntilStale == 0L) {
            log.trace("Freshness checking disable by properties: {}", airportProperties)
            return airport.toMono()
        }
        val stalenessDate = LocalDateTime.now().minusDays(airportProperties.daysUntilStale)
        return when (airport.modified.isBefore(stalenessDate)) {
            true -> {
                log.trace("Attempting refresh on airport: {}", airport)
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
            false -> {
                log.debug("Airport still fresh")
                airport.toMono()
            }
        }
    }

    private fun findAirport(airportCode: AirportCode): Optional<Airport> {
        return when (airportCode.codeType) {
            CodeType.IATA -> airportRepository.findByIataIgnoreCase(airportCode.code)
            CodeType.ICAO -> airportRepository.findByIcaoIgnoreCase(airportCode.code)
        }
    }

    private fun saveAirport(airportApiDTO: AirportApiDTO): Airport {
        log.trace("Saving airport from dto: {}", airportApiDTO)
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
        log.trace("Updating airport: {}", airport)
        return airportRepository.save(airport)
    }

    private fun getPoint(airportApiDTO: AirportApiDTO): Point {
        return Point(airportApiDTO.longitude.toDouble(), airportApiDTO.latitude.toDouble())
    }
}
