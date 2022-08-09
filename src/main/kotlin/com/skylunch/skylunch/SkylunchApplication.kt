package com.skylunch.skylunch

import com.skylunch.skylunch.airport.AirportProperties
import com.skylunch.skylunch.redis.RedisProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(value = [
    AirportProperties::class,
    RedisProperties::class,
])
@SpringBootApplication
class SkylunchApplication

fun main(args: Array<String>) {
    runApplication<SkylunchApplication>(*args)
}
