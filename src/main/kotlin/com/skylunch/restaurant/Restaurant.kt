package com.skylunch.restaurant

import com.redis.om.spring.annotations.Document
import com.redis.om.spring.annotations.Indexed
import org.springframework.data.annotation.Id
import org.springframework.data.geo.Point
import java.time.LocalDateTime

/**
 * The restaurant document model that is stored by redis.
 * [modified] should be updated to indicate the freshness or staleness of a query.
 */
@Document
class Restaurant(
    @Id
    @Indexed
    val id: String = "",
    @Indexed
    val address: String,
    @Indexed
    val phoneNumber: String?,
    @Indexed
    val name: String?,
    @Indexed
    val rating: String?,
    @Indexed
    val url: String?,
    @Indexed
    val totalRating: String?,
    @Indexed
    val website: String?,
    @Indexed
    val location: Point,
    @Indexed
    var modified: LocalDateTime = LocalDateTime.now(),
)
