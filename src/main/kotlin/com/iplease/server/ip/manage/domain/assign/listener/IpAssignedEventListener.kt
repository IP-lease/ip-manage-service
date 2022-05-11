package com.iplease.server.ip.manage.domain.assign.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.domain.assign.handler.IpAssignedEventHandler
import com.iplease.server.ip.manage.infra.event.data.dto.IpAssignedEvent
import com.iplease.server.ip.manage.infra.event.listener.EventListener
import com.iplease.server.ip.manage.infra.event.service.EventSubscribeService
import com.iplease.server.ip.manage.infra.event.data.type.Event
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component

@Component
class IpAssignedEventListener(
    private val ipAssignedEventHandler: IpAssignedEventHandler,
    eventSubscribeService: EventSubscribeService
): EventListener {
    init { eventSubscribeService.addListener(this) }

    override fun handle(message: Message) {
        if(message.messageProperties.receivedRoutingKey != Event.IP_ASSIGNED.routingKey) return
        ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .readValue(message.body, IpAssignedEvent::class.java)
            .toDto()
            .let{ ipAssignedEventHandler.handle(it) }
    }
}