package com.iplease.server.ip.manage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
class IpManageServiceApplication {
    @Bean
    fun webClientBuilder(function: ReactorLoadBalancerExchangeFilterFunction): WebClient.Builder =
        WebClient.builder()
            .filter(function)
}

fun main(args: Array<String>) {
    runApplication<IpManageServiceApplication>(*args)
}
