package com.iplease.server.ip.manage.domain.assign.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.domain.assign.handler.IpAssignedEventHandler
import com.iplease.server.ip.manage.infra.event.data.dto.IpAssignedEvent
import com.iplease.server.ip.manage.infra.event.data.dto.WrongPayloadError
import com.iplease.server.ip.manage.infra.event.data.type.Error
import com.iplease.server.ip.manage.infra.event.listener.EventListener
import com.iplease.server.ip.manage.infra.event.service.EventSubscribeService
import com.iplease.server.ip.manage.infra.event.data.type.Event
import com.iplease.server.ip.manage.infra.event.service.EventPublishService
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class IpAssignedEventListener(
    private val ipAssignedEventHandler: IpAssignedEventHandler,
    private val eventPublishService: EventPublishService,
    eventSubscribeService: EventSubscribeService
): EventListener {
    init { eventSubscribeService.addListener(this) }

    override fun handle(message: Message) {
        if(message.messageProperties.receivedRoutingKey != Event.IP_ASSIGNED.routingKey) return
        ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .toMono()
            .map{ it.readValue(message.body, IpAssignedEvent::class.java) }
            .doOnError{
                eventPublishService.publish(
                    Error.WRONG_PAYLOAD.routingKey,
                    WrongPayloadError(Event.IP_ASSIGNED, message.body.toString())
                )
            }.map{ it.toDto() }
            .flatMap { ipAssignedEventHandler.handle(it) }
            .block()
    }
}