package com.skylunch.security

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * Filters request based on a custom header key/value defined in [securityProperties].
 */
@Component
class HeaderBasedWebFilter(private val securityProperties: SecurityProperties) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val securityHeaderValue = exchange.request.headers.getFirst(securityProperties.securityHeaderKey)
        return chain.filter(
            when (securityHeaderValue == securityProperties.securityHeaderValue) {
                true -> exchange
                false -> {
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    exchange
                }
            }
        )
    }
}
