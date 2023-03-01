package com.skylunch

import com.skylunch.airport.Airport
import com.skylunch.airport.AirportProperties
import com.skylunch.airport.airportApi.AirportApiDTO
import com.skylunch.airport.airportApi.AirportApiProperties
import com.skylunch.airport.airportApi.AirportApiService
import com.skylunch.restaurant.Restaurant
import com.skylunch.restaurant.RestaurantProperties
import com.skylunch.restaurant.restaurantApi.Geometry
import com.skylunch.restaurant.restaurantApi.LatLngLiteral
import com.skylunch.restaurant.restaurantApi.Place
import com.skylunch.restaurant.restaurantApi.RestaurantApiDTO
import com.skylunch.restaurant.restaurantApi.RestaurantApiProperties
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.geoLocation
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.ipAddressV4
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import okhttp3.mockwebserver.MockResponse
import org.springframework.data.geo.Point
import org.springframework.web.reactive.function.client.WebClient
import java.net.URL
import java.time.LocalDateTime

/**
 * Arbitrary [URL] with assumed valid fields.
 */
val URLarb = arbitrary {
    URL(
        "https",
        Arb.ipAddressV4().bind(),
        Arb.int(1, UShort.MAX_VALUE.toInt()).bind(),
        "/"
    )
}

/**
 * Arbitrary [Airport] with assumed valid fields.
 */
val airportArb = arbitrary {
    val code = Arb.string(3, Codepoint.az()).bind().uppercase()
    Airport(
        id = Arb.string(12, Codepoint.az()).bind(),
        icao = "K$code",
        iata = code,
        name = Arb.string(3, 30, Codepoint.az()).bind(),
        location = pointArb.bind(),
        modified = LocalDateTime.now()
    )
}

/**
 * Arbitrary [AirportApiDTO] with assumed valid fields.
 */
val airportApiDTOArb = arbitrary {
    val code = Arb.string(3, Codepoint.az()).bind().uppercase()
    AirportApiDTO(
        icao = "K$code",
        iata = code,
        name = "$code airport",
        latitude = Arb.geoLocation().bind().latitude.toString(),
        longitude = Arb.geoLocation().bind().longitude.toString()
    )
}

/**
 * Arbitrary [Point] with assumed valid fields.
 */
val pointArb = arbitrary {
    Point(
        Arb.geoLocation().bind().longitude,
        Arb.geoLocation().bind().latitude
    )
}

/**
 * Arbitrary [Restaurant] with assumed valid fields.
 */
val restaurantArb = arbitrary {
    Restaurant(
        id = Arb.string(12, Codepoint.az()).bind(),
        address = Arb.stringPattern(
            "[0-9]{1,5}( [a-zA-Z.]*){1,4},?( [a-zA-Z]*){1,3},? [a-zA-Z]{2},? [0-9]{5}"
        ).bind(),
        phoneNumber = Arb.stringPattern(
            "^[1-9]\\d{2}-\\d{3}-\\d{4}"
        ).bind(),
        name = Arb.string(3, 55, Codepoint.az()).bind(),
        rating = it.random.nextInt(0, 5).toString(),
        url = "https://www.google.com/places?id=${Arb.string(10, Codepoint.az()).bind()}",
        totalRating = it.random.nextInt(0, 5).toString(),
        website = URLarb.bind().toString(),
        location = pointArb.bind(),
        modified = Arb.localDateTime(LocalDateTime.now().minusYears(20L), LocalDateTime.now()).bind()
    )
}

/**
 * Arbitrary [RestaurantApiDTO] with assumed valid fields.
 */
val restaurantApiDTOArb = arbitrary {
    RestaurantApiDTO(
        status = "OK",
        results = listOf(placeArb.bind(), placeArb.bind(), placeArb.bind())
    )
}

/**
 * Arbitrary [Place] with assumed valid fields.
 */
val placeArb = arbitrary {
    val source = restaurantArb.bind()
    Place(
        address = source.address,
        phoneNumber = source.phoneNumber,
        rating = source.rating,
        totalRating = source.totalRating,
        url = source.url,
        website = source.website,
        name = source.name,
        geometry = Geometry(
            location = LatLngLiteral(
                lat = source.location.y,
                lng = source.location.x
            )
        )
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
            apiKey = "key"
        ),
        daysUntilStale = daysUntilStale
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
            apiKey = "key"
        ),
        daysUntilStale = daysUntilStale
    )
}
