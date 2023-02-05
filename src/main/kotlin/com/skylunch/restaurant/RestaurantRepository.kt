package com.skylunch.restaurant

import com.redis.om.spring.repository.RedisDocumentRepository
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point

interface RestaurantRepository : RedisDocumentRepository<Restaurant, String> {
    fun findByLocationNear(location: Point, radius: Distance): Iterable<Restaurant>
}
