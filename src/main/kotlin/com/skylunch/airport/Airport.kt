package com.skylunch.airport

import com.redis.om.spring.annotations.Document
import com.redis.om.spring.annotations.Indexed
import org.springframework.data.annotation.Id
import org.springframework.data.geo.Point
import java.time.LocalDateTime

@Document
data class Airport(
    @Id
    @Indexed
    val id: String = "",
    @Indexed
    val icao: String?,
    @Indexed
    val iata: String?,
    @Indexed
    val name: String?,
    @Indexed
    val location: Point,
    @Indexed
    var modified: LocalDateTime = LocalDateTime.now(),
)
