package com.iplease.server.ip.manage.log

import com.iplease.server.ip.manage.event.listener.EventListener
import com.iplease.server.ip.manage.event.service.EventSubscribeService
import com.iplease.server.ip.manage.log.service.LoggingService
import com.iplease.server.ip.manage.log.type.LoggerType
import com.iplease.server.ip.manage.log.util.EventSubscribeInput
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