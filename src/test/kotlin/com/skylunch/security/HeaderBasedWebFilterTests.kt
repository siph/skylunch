package com.skylunch.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN
import org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class HeaderBasedWebFilterTests {

    @Test
    fun `assert that present header value passes`() {
        val properties = getMockSecurityProperties()
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/")
                .header(properties.securityHeaderKey, properties.securityHeaderValue)
        )
        val chain = getChain()
        HeaderBasedWebFilter(getMockSecurityProperties()).filter(exchange, chain).block()
        assertThat(exchange.response.statusCode).isNull()
    }

    @Test
    fun `assert that absent header value fails`() {
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"))
        val chain = getChain()
        HeaderBasedWebFilter(getMockSecurityProperties()).filter(exchange, chain).block()
        assertThat(exchange.response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `assert that wrong header value fails`() {
        val properties = getMockSecurityProperties()
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/")
                .header(properties.securityHeaderKey, properties.securityHeaderValue + "_tail")
        )
        val chain = getChain()
        HeaderBasedWebFilter(getMockSecurityProperties()).filter(exchange, chain).block()
        assertThat(exchange.response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    private fun getChain() = WebFilterChain { filterExchange: ServerWebExchange ->
        try {
            val headers: HttpHeaders = filterExchange.response.headers
            assertThat(headers.getFirst(ACCESS_CONTROL_ALLOW_ORIGIN)).isNull()
            assertThat(headers.getFirst(ACCESS_CONTROL_EXPOSE_HEADERS)).isNull()
        } catch (ex: AssertionError) {
            return@WebFilterChain Mono.error<Void?>(ex)
        }
        Mono.empty()
    }
    private fun getMockSecurityProperties() = SecurityProperties("key", "value")
}
