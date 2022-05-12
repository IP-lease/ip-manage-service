package com.iplease.server.ip.manage.domain.release.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.iplease.server.ip.manage.domain.release.handler.IpReleaseEventHandler
import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedEvent
import com.iplease.server.ip.manage.infra.message.data.dto.WrongPayloadError
import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.listener.MessageListener
import com.iplease.server.ip.manage.infra.message.service.MessagePublishService
import com.iplease.server.ip.manage.infra.message.service.MessageSubscribeService
import org.springframework.amqp.core.Message
import reactor.kotlin.core.publisher.toMono

class IpReleasedMessageListener(
    private val ipReleaseEventHandler: IpReleaseEventHandler,
    private val messagePublishService: MessagePublishService,
    messageSubscribeService: MessageSubscribeService
): MessageListener {
    init {
        messageSubscribeService.addListener(this)
    }

    override fun handle(message: Message) {
        if (message.messageProperties.receivedRoutingKey != Event.IP_RELEASED.routingKey) return
        ObjectMapper()
            .registerKotlinModule()
            .toMono()
            .map { it.readValue(message.body, IpReleasedEvent::class.java) }
            .onErrorContinue{_, _ ->
                messagePublishService.publish(
                    Error.WRONG_PAYLOAD.routingKey,
                    WrongPayloadError(Event.IP_RELEASED, message.body.toString())
                )
            }
            .map { it.toDto()}
            .flatMap { ipReleaseEventHandler.handle(it, Unit) }
            .block()
    }
}
