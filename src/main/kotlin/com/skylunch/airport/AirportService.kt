package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiDTO
import com.skylunch.airport.airportApi.AirportApiService
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service
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
    fun getAirport(airportCode: AirportCode): Optional<Airport> {
        when (airportIsCached(airportCode)) {
            true -> {
                findAirport(airportCode).map { refreshAirport(it) }
            }
            false -> {
                airportApiService
                    .getAirport(airportCode)
                    .map { it.first() }
                    .subscribe(this::saveAirport)
            }
        }
        return findAirport(airportCode)
    }

    private fun refreshAirport(airport: Airport) {
        if (airportProperties.daysUntilStale == 0L) { return }
        val stalenessDate = LocalDateTime.now().minusDays(airportProperties.daysUntilStale)
        if (airport.modified.isBefore(stalenessDate)) {
            airportApiService
                .getAirport(airportToAirportCode(airport))
                .map { it.first() }
                .subscribe {
                    updateAirport(
                        Airport(
                            id = airport.id,
                            iata = it.iata?.uppercase(),
                            icao = it.icao?.uppercase(),
                            name = it.name,
                            location = getPoint(it),
                            modified = LocalDateTime.now(),
                        )
                    )
                }
        }
    }

    private fun findAirport(airportCode: AirportCode): Optional<Airport> {
        return when (airportCode.codeType) {
            CodeType.IATA -> airportRepository.findAirportByIata(airportCode.code)
            CodeType.ICAO -> airportRepository.findAirportByIcao(airportCode.code)
        }
    }

    private fun airportIsCached(airportCode: AirportCode): Boolean {
        return when (airportCode.codeType) {
            CodeType.IATA -> airportRepository.existsByIata(airportCode.code)
            CodeType.ICAO -> airportRepository.existsByIcao(airportCode.code)
        }
    }

    private fun saveAirport(airportApiDTO: AirportApiDTO): Airport {
        return airportRepository.save(
            Airport(
                iata = airportApiDTO.iata?.uppercase(),
                icao = airportApiDTO.icao?.uppercase(),
                name = airportApiDTO.name,
                location = getPoint(airportApiDTO)
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
