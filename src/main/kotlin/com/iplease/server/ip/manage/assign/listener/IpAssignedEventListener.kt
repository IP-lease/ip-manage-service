package com.iplease.server.ip.manage.assign.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.assign.dto.AssignedIpDto
import com.iplease.server.ip.manage.assign.service.IpAssignService
import com.iplease.server.ip.manage.event.dto.IpAssignedEvent
import com.iplease.server.ip.manage.event.listener.EventListener
import com.iplease.server.ip.manage.event.service.EventSubscribeService
import com.iplease.server.ip.manage.event.type.Event
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component

@Component
class IpAssignedEventListener(
    private val ipAssignService: IpAssignService,
    eventSubscribeService: EventSubscribeService
): EventListener {
    init { eventSubscribeService.addListener(this) }

    override fun handle(event: String, message: Message) {
        if(message.messageProperties.receivedRoutingKey != Event.IP_ASSIGNED.routingKey) return
        ObjectMapper().registerModule(KotlinModule()).readValue(message.body, IpAssignedEvent::class.java)
            .let { AssignedIpDto(0L, it.issuerUuid, it.assignerUuid, it.assignedAt, it.expireAt) }
            .let { ipAssignService.assign(it) }
            .doOnError{  } //TODO 에러처리로직 작성하기
            .block()
    }
}