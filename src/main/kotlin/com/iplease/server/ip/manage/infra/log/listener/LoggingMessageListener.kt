package com.iplease.server.ip.manage.infra.log.listener

import com.iplease.server.ip.manage.infra.message.listener.MessageListener
import com.iplease.server.ip.manage.infra.message.service.MessageSubscribeService
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.log.type.LoggerType
import com.iplease.server.ip.manage.infra.log.util.EventSubscribeInput
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class LoggingMessageListener(
    messageSubscribeService: MessageSubscribeService,
    private val loggingService: LoggingService
): MessageListener {
    init {
        messageSubscribeService.addListener(this)
    }
    override fun handle(message: Message) {
        loggingService.withLog(
            EventSubscribeInput(message.messageProperties.receivedRoutingKey, message.body!!.toString(Charsets.UTF_8)),
            Unit.toMono(),
            LoggerType.EVENT_SUBSCRIBE_LOGGER
        ).block()
    }
}