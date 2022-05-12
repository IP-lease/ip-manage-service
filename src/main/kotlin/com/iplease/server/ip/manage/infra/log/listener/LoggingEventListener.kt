package com.iplease.server.ip.manage.infra.log.listener

import com.iplease.server.ip.manage.infra.message.listener.EventListener
import com.iplease.server.ip.manage.infra.message.service.EventSubscribeService
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.log.type.LoggerType
import com.iplease.server.ip.manage.infra.log.util.EventSubscribeInput
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class LoggingEventListener(
    eventSubscribeService: EventSubscribeService,
    private val loggingService: LoggingService
): EventListener {
    init {
        eventSubscribeService.addListener(this)
    }
    override fun handle(message: Message) {
        loggingService.withLog(
            EventSubscribeInput(message.messageProperties.receivedRoutingKey, message.body!!.toString(Charsets.UTF_8)),
            Unit.toMono(),
            LoggerType.EVENT_SUBSCRIBE_LOGGER
        ).block()
    }
}