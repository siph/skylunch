package com.skylunch.http

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class SearchGraphQLController(private val searchService: SearchService) {
    @QueryMapping
    fun restaurantsByCode(code: String) = searchService.findRestaurantsByAirportCode(code)
}
