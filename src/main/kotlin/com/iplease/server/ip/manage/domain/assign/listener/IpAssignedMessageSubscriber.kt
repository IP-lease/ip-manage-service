package com.iplease.server.ip.manage.domain.assign.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.domain.assign.handler.IpAssignedEventHandler
import com.iplease.server.ip.manage.infra.message.data.dto.IpAssignedEvent
import com.iplease.server.ip.manage.infra.message.data.dto.WrongPayloadError
import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.listener.MessageSubscriber
import com.iplease.server.ip.manage.infra.message.service.MessageSubscribeService
import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.service.MessagePublishService
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class IpAssignedMessageSubscriber(
    private val ipAssignedEventHandler: IpAssignedEventHandler,
    private val messagePublishService: MessagePublishService,
    messageSubscribeService: MessageSubscribeService
): MessageSubscriber {
    init { messageSubscribeService.addListener(this) }

    override fun subscribe(message: Message) {
        if(message.messageProperties.receivedRoutingKey != Event.IP_ASSIGNED.routingKey) return
        ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .toMono()
            .map{ it.readValue(message.body, IpAssignedEvent::class.java) }
            .onErrorContinue {_, _ ->
                messagePublishService.publish(
                    Error.WRONG_PAYLOAD,
                    WrongPayloadError(Event.IP_ASSIGNED, message.body.toString())
                )
            }.map{ it.toDto() }
            .flatMap { ipAssignedEventHandler.handle(it, it) }
            .block()
    }
}