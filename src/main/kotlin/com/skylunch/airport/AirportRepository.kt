package com.skylunch.airport

import com.redis.om.spring.repository.RedisDocumentRepository
import java.util.*

interface AirportRepository: RedisDocumentRepository<Airport, String> {
    fun findAirportByIata(iata: String): Optional<Airport>
    fun findAirportByIcao(icao: String): Optional<Airport>
}
