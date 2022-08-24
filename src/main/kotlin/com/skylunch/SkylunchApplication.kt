package com.skylunch

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories
import com.skylunch.airport.AirportProperties
import com.skylunch.redis.RedisProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableRedisDocumentRepositories
@EnableConfigurationProperties(value = [
    AirportProperties::class,
    RedisProperties::class,
])
@SpringBootApplication
class SkylunchApplication

fun main(args: Array<String>) {
    runApplication<SkylunchApplication>(*args)
}
