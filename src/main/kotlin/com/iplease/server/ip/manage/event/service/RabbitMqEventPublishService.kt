package com.iplease.server.ip.manage.event.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.log.service.LoggingService
import com.iplease.server.ip.manage.log.type.LoggerType
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono

@Service
class RabbitMqEventPublishService(
    val rabbitTemplate: RabbitTemplate,
    val loggingService: LoggingService
): EventPublishService {
    companion object { const val EXCHANGE_NAME = "iplease.event" }
    override fun <T: Any> publish(routingKey: String, data: T): T =
        ObjectMapper().registerModule(KotlinModule()).writeValueAsString(data)
            .let { rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, it) }
            .let { data }
            .let { loggingService.withLog(it, data.toMono(), LoggerType.EVENT_PUBLISH_LOGGER) }
            .block()!!
}