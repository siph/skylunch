package com.skylunch.http

import com.skylunch.pointArb
import com.skylunch.restaurantArb
import io.kotest.common.runBlocking
import io.kotest.property.checkAll
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SearchDTOTests {

    @Test
    fun `Assert Restaurant converts to SearchDTO`() {
        runBlocking {
            checkAll(restaurantArb) { restaurant ->
                val expectedDto = SearchDTO(
                    name = restaurant.name,
                    address = restaurant.address,
                    phoneNumber = restaurant.phoneNumber,
                    rating = restaurant.rating,
                    totalRating = restaurant.totalRating,
                    url = restaurant.url,
                    website = restaurant.website,
                    coords = restaurant.location.toCoords()
                )
                Assertions.assertThat(restaurant.toSearchDTO()).isEqualTo(expectedDto)
            }
        }
    }

    @Test
    fun `Assert Point converts to Location`() {
        runBlocking {
            checkAll(pointArb) {
                val location = it.toCoords()
                Assertions.assertThat(location.longitude).isEqualTo(it.x.toString())
                Assertions.assertThat(location.latitude).isEqualTo(it.y.toString())
            }
        }
    }
}
