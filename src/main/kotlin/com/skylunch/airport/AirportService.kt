package com.skylunch.airport

import com.skylunch.airport.airportApi.AirportApiDTO
import com.skylunch.airport.airportApi.AirportApiService
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*


@Service
class AirportService(
    private val airportApiService: AirportApiService,
    private val airportRepository: AirportRepository,
    private val airportProperties: AirportProperties,
) {

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
                            iata = it.iata,
                            icao = it.icao,
                            name = it.name,
                            location = getPoint(it),
                        )
                    )
                }
        }
    }

    private fun findAirport(airportCode: AirportCode): Optional<Airport> {
        return when (airportCode.codeType) {
            CodeType.ICAO -> airportRepository.findAirportByIata(airportCode.code)
            CodeType.IATA -> airportRepository.findAirportByIcao(airportCode.code)
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
                iata = airportApiDTO.iata,
                icao = airportApiDTO.icao,
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
