package com.skylunch.airport

data class AirportCode(
    val code: String,
    val codeType: CodeType,
)

enum class CodeType(val string: String) {
    IATA("iata"),
    ICAO("icao"),
}

fun getAirportCodeType(code: String): CodeType {
    return when (code.length) {
        3 -> CodeType.IATA
        4 -> CodeType.ICAO
        else -> throw Exception("Airport code must be 3 or 4 characters")
    }
}

fun airportToAirportCode(airport: Airport): AirportCode {
    val code: String = airport.iata ?: airport.icao!!
    return AirportCode(code, getAirportCodeType(code))
}
