package com.iplease.server.ip.manage.domain.release.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.iplease.server.ip.manage.domain.release.handler.IpReleaseEventHandler
import com.iplease.server.ip.manage.infra.event.data.dto.IpReleasedEvent
import com.iplease.server.ip.manage.infra.event.data.dto.WrongPayloadError
import com.iplease.server.ip.manage.infra.event.data.type.Error
import com.iplease.server.ip.manage.infra.event.data.type.Event
import com.iplease.server.ip.manage.infra.event.listener.EventListener
import com.iplease.server.ip.manage.infra.event.service.EventPublishService
import com.iplease.server.ip.manage.infra.event.service.EventSubscribeService
import org.springframework.amqp.core.Message
import reactor.kotlin.core.publisher.toMono

class IpReleasedEventListener(
    private val ipReleaseEventHandler: IpReleaseEventHandler,
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
            .toMono()
            .map { it.readValue(message.body, IpReleasedEvent::class.java) }
            .onErrorContinue{_, _ ->
                eventPublishService.publish(
                    Error.WRONG_PAYLOAD.routingKey,
                    WrongPayloadError(Event.IP_RELEASED, message.body.toString())
                )
            }
            .map { it.toDto()}
            .flatMap { ipReleaseEventHandler.handle(it, Unit) }
            .block()
    }
}
