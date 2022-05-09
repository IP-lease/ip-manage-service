package com.iplease.server.ip.manage.assign.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.assign.dto.AssignedIpDto
import com.iplease.server.ip.manage.assign.service.IpAssignService
import com.iplease.server.ip.manage.event.dto.IpAssignedError
import com.iplease.server.ip.manage.event.dto.IpAssignedEvent
import com.iplease.server.ip.manage.event.listener.EventListener
import com.iplease.server.ip.manage.event.service.EventPublishService
import com.iplease.server.ip.manage.event.service.EventSubscribeService
import com.iplease.server.ip.manage.event.type.Error
import com.iplease.server.ip.manage.event.type.Event
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component

@Component
class IpAssignedEventListener(
    private val ipAssignService: IpAssignService,
    private val eventPublishService: EventPublishService,
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
            .let { assign(it) }
    }

    private fun assign(dto: AssignedIpDto) {
        ipAssignService.assign(dto)
            .doOnError{eventPublishService.publish(Error.IP_ASSIGNED.routingKey, dto.error(it)) }
            .onErrorReturn(dto)
            .block()
    }

    private fun AssignedIpDto.error(throwable: Throwable) = IpAssignedError(
        issuerUuid, assignerUuid, assignedAt, expireAt,
        ip.first, ip.second, ip.third, ip.fourth,
        throwable
    )
}