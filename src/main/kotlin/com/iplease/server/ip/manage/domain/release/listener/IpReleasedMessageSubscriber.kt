package com.iplease.server.ip.manage.domain.release.listener

import com.iplease.server.ip.manage.domain.release.handler.IpReleaseEventHandler
import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedEvent
import com.iplease.server.ip.manage.infra.message.listener.EventMessageSubscriberV2
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishServiceFacade
import com.iplease.server.ip.manage.infra.message.service.subscribe.MessageSubscribeService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpReleasedMessageSubscriber(
    private val ipReleaseEventHandler: IpReleaseEventHandler,
    messagePublishService: MessagePublishServiceFacade,
    messageSubscribeService: MessageSubscribeService
): EventMessageSubscriberV2<IpReleasedEvent>(IpReleasedEvent::class, messagePublishService, messageSubscribeService) {
    override fun handle(event: Mono<IpReleasedEvent>) = event.map { it.toDto()}.flatMap { ipReleaseEventHandler.handle(it, Unit) }
}
