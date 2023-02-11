package com.skylunch.http

import com.skylunch.restaurant.Restaurant
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.geo.Point
import java.time.LocalDateTime

class SearchDTOTests {

    @Test
    fun `Assert Restaurant converts to SearchDTO`() {
        val restaurant = Restaurant(
            id = "id",
            address = "123",
            phoneNumber = "123456789",
            name = "chum bucket",
            rating = "1",
            url = "https://www.google.com/result?id=553",
            totalRating = null,
            website = "https://chum.bucket",
            location = Point("-118.4079971".toDouble(), "33.94250107".toDouble()),
            modified = LocalDateTime.now()
        )
        val expectedDto = SearchDTO(
            name = "chum bucket",
            address = "123",
            phoneNumber = "123456789",
            rating = "1",
            totalRating = null,
            url = "https://www.google.com/result?id=553",
            website = "https://chum.bucket",
            coords = Coords("33.94250107", "-118.4079971")
        )
        Assertions.assertThat(restaurant.toSearchDTO()).isEqualTo(expectedDto)
    }
}
