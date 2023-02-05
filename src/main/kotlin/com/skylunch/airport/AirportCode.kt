package com.skylunch.airport

import com.skylunch.airport.CodeType.IATA
import com.skylunch.airport.CodeType.ICAO

/**
 * Holds the three or four digit [code] for an airport and its corresponding [codeType].
 */
data class AirportCode(
    val code: String,
    val codeType: CodeType
)

/**
 * Skylunch handles both the three digit [IATA] and four digit [ICAO] airport codes when searching airports.
 */
enum class CodeType(val string: String) {
    IATA("iata"),
    ICAO("icao")
}

/**
 * Returns the [CodeType] that corresponds to the [code].
 */
fun getAirportCodeType(code: String): CodeType {
    return when (code.length) {
        3 -> IATA
        4 -> ICAO
        else -> throw Exception("Airport code must be 3 or 4 characters")
    }
}

/**
 * Returns the [AirportCode] that corresponds to the [airport].
 * Favors an [IATA] code over an [ICAO] code.
 */
fun airportToAirportCode(airport: Airport): AirportCode {
    val code: String = airport.iata ?: airport.icao!!
    return AirportCode(code, getAirportCodeType(code))
}
