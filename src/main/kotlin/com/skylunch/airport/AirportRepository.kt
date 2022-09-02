package com.skylunch.airport

import com.redis.om.spring.repository.RedisDocumentRepository
import java.util.*

interface AirportRepository: RedisDocumentRepository<Airport, String> {
    fun findByIataIgnoreCase(iata: String): Optional<Airport>
    fun findByIcaoIgnoreCase(icao: String): Optional<Airport>
}
