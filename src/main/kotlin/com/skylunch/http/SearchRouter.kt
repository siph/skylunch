package com.skylunch.http

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class SearchRouter(private val searchHandler: SearchHandler) {

    @Bean
    fun search() = router {
        (accept(MediaType.TEXT_HTML) and "/api/v1").nest {
            (GET("/search") or POST("/search"))
                .invoke(searchHandler::findRestaurantsByAirportCode)
        }
    }
}
