package com.iplease.server.ip.manage.domain.assign.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.domain.assign.service.IpAssignService
import com.iplease.server.ip.manage.global.release.IpReleaseReserveService
import com.iplease.server.ip.manage.infra.event.data.dto.IpAssignedError
import com.iplease.server.ip.manage.infra.event.data.dto.IpAssignedEvent
import com.iplease.server.ip.manage.infra.event.listener.EventListener
import com.iplease.server.ip.manage.infra.event.service.EventPublishService
import com.iplease.server.ip.manage.infra.event.service.EventSubscribeService
import com.iplease.server.ip.manage.infra.event.data.type.Error
import com.iplease.server.ip.manage.infra.event.data.type.Event
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component

@Component
class IpAssignedEventListener(
    private val ipAssignService: IpAssignService,
    private val eventPublishService: EventPublishService,
    private val ipReleaseReserveService: IpReleaseReserveService,
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
            .also { reserveRelease(it) }
    }

    //TODO 로직 처리중에 에러가 날경우 이를 전파해야할지 아니면 예약이 안된상태로 IP를 할당해야하는지 고민해보기
    private fun reserveRelease(dto: AssignedIpDto) {
        ipReleaseReserveService.reserve(dto.uuid, dto.issuerUuid, dto.expireAt)
    }

    private fun assign(dto: AssignedIpDto) =
        ipAssignService.assign(dto)
            .doOnError{eventPublishService.publish(Error.IP_ASSIGNED.routingKey, dto.error(it)) }
            .onErrorReturn(dto)
            .block()!!

    private fun AssignedIpDto.error(throwable: Throwable) = IpAssignedError(
        issuerUuid, assignerUuid, assignedAt, expireAt,
        ip.first, ip.second, ip.third, ip.fourth,
        throwable
    )
}