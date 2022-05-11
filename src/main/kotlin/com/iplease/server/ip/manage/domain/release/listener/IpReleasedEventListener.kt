package com.iplease.server.ip.manage.domain.release.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.iplease.server.ip.manage.domain.release.service.IpReleaseService
import com.iplease.server.ip.manage.infra.event.data.dto.IpReleasedError
import com.iplease.server.ip.manage.infra.event.data.dto.IpReleasedEvent
import com.iplease.server.ip.manage.infra.event.data.type.Error
import com.iplease.server.ip.manage.infra.event.data.type.Event
import com.iplease.server.ip.manage.infra.event.listener.EventListener
import com.iplease.server.ip.manage.infra.event.service.EventPublishService
import com.iplease.server.ip.manage.infra.event.service.EventSubscribeService
import org.springframework.amqp.core.Message

class IpReleasedEventListener(
    private val ipReleaseService: IpReleaseService,
    private val eventPublishService: EventPublishService,
    eventSubscribeService: EventSubscribeService
): EventListener {
    init {
        eventSubscribeService.addListener(this)
    }

    override fun handle(message: Message) {
        if (message.messageProperties.receivedRoutingKey != Event.IP_RELEASED.routingKey) return
        ObjectMapper()
            .registerKotlinModule()
            .runCatching { readValue(message.body, IpReleasedEvent::class.java) }
            .onFailure { throwable -> eventPublishService.publish(Error.IP_RELEASED.routingKey, IpReleasedError(0L, 0L, throwable)) }
            .onSuccess { release(it) }
    }

    private fun release(event: IpReleasedEvent) {
        ipReleaseService.release(event.assignedIpUuid)
            .doOnError { eventPublishService.publish(Error.IP_RELEASED.routingKey, event.error(it)) }
            .onErrorReturn(Unit)
            .block()
    }

    private fun IpReleasedEvent.error(it: Throwable) = IpReleasedError(assignedIpUuid, issuerUuid, it)
}
