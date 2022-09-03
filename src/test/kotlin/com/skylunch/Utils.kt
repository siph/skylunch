package com.skylunch

import com.skylunch.airport.Airport
import com.skylunch.airport.AirportCode
import com.skylunch.airport.AirportProperties
import com.skylunch.airport.airportApi.AirportApiDTO
import com.skylunch.airport.airportApi.AirportApiProperties
import com.skylunch.airport.airportApi.AirportApiService
import com.skylunch.airport.getAirportCodeType
import com.skylunch.restaurant.RestaurantProperties
import com.skylunch.restaurant.restaurantApi.*
import okhttp3.mockwebserver.MockResponse
import org.springframework.data.geo.Point
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

/**
 * Returns a mock [AirportApiDTO].
 * The default value is the actual returned value for 'lax'.
 * @param modified last updated date.
 * @return mock [Airport].
 */
fun getMockAirport(modified: LocalDateTime = LocalDateTime.now()): Airport {
    return Airport(
        id = "1",
        icao = "KLAX",
        iata = "LAX",
        name = "Los Angeles International Airport",
        location = Point("-118.4079971".toDouble(), "33.94250107".toDouble()),
        modified = modified
    )
}

/**
 * Returns a [MockResponse] for external [WebClient] calls to receive.
 * @param body json string to populate the body.
 * @return response to queue for service tests.
 */
fun getMockResponseOK(body: String): MockResponse {
    return MockResponse()
        .setResponseCode(200)
        .setBody(body)
        .setHeader("content-type", "application/json")
        .setHeader("content-length", body.length)
}

/**
 * Returns a mock [AirportProperties].
 * The default apiKey and daysUntilStale is 'key' and 0 respectively.
 * @param baseUrl the url that is injected into the [AirportApiService].
 * @param daysUntilStale refresh interval
 * @return mock properties.
 */
fun getMockAirportProperties(baseUrl: String, daysUntilStale: Long = 0L): AirportProperties {
    return AirportProperties(
        api = AirportApiProperties(
            baseUrl = baseUrl,
            apiKey = "key",
        ),
        daysUntilStale = daysUntilStale
    )
}

/**
 * Returns a mock [AirportCode].
 * The default code is 'LAX' and the [com.skylunch.airport.CodeType] property is dynamically generated.
 * @param code three or four digit airport code.
 * @return mock [AirportCode].
 */
fun getMockAirportCode(code: String = "LAX"): AirportCode {
    return AirportCode(code, getAirportCodeType(code))
}

/**
 * Returns a mock [AirportApiDTO].
 * The default value is the actual returned value for 'lax'.
 * @return mock [AirportApiDTO].
 */
fun getMockAirportApiDTO(): AirportApiDTO {
    return AirportApiDTO(
        icao = "KLAX",
        iata = "LAX",
        name = "Los Angeles International Airport",
        latitude = "33.94250107",
        longitude = "-118.4079971",
    )
}

/**
 * Returns a mock [RestaurantProperties].
 * The default apiKey and daysUntilStale is 'key' and 0 respectively.
 * @param baseUrl the url that is injected into the [AirportApiService].
 * @param daysUntilStale refresh interval
 * @return mock properties.
 */
fun getMockRestaurantProperties(baseUrl: String, daysUntilStale: Long = 0L): RestaurantProperties {
    return RestaurantProperties(
        api = RestaurantApiProperties(
            baseUrl = baseUrl,
            apiKey = "key",
        ),
        daysUntilStale = daysUntilStale,
    )
}

/**
 * Returns a mock [RestaurantApiDTO].
 * The default value is the actual returned value for 'lax'.
 * @return mock [RestaurantApiDTO].
 */
fun getMockRestaurantApiDTO(): RestaurantApiDTO {
    return RestaurantApiDTO(
        status = "OK",
        results = arrayListOf(getMockPlace()),
    )
}

/**
 * @return mock [CandidatesDTO].
 */
fun getMockCandidatesDTO(): CandidatesDTO {
    return CandidatesDTO(
        candidates = listOf(getMockPlace())
    )
}

/**
 * @return mock [Place].
 */
fun getMockPlace(): Place {
    return Place(
        address = "123 street",
        phoneNumber = "number",
        rating = "rating",
        totalRating = "total rating",
        url = "google url",
        website = "website",
        name = "restaurant",
        geometry = Geometry(
            location = LatLngLiteral(
                lat = 38.8409,
                lng = 105.0423,
            )
        )
    )
}
