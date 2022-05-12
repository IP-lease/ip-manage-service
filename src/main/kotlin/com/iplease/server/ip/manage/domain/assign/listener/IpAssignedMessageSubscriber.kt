package com.iplease.server.ip.manage.domain.assign.listener

import com.iplease.server.ip.manage.domain.assign.handler.IpAssignedEventHandler
import com.iplease.server.ip.manage.infra.message.data.dto.IpAssignedEvent
import com.iplease.server.ip.manage.infra.message.service.subscribe.MessageSubscribeService
import com.iplease.server.ip.manage.infra.message.listener.EventMessageSubscriberV2
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishServiceFacade
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpAssignedMessageSubscriber(
    private val ipAssignedEventHandler: IpAssignedEventHandler,
    messagePublishService: MessagePublishServiceFacade,
    messageSubscribeService: MessageSubscribeService
): EventMessageSubscriberV2<IpAssignedEvent>(IpAssignedEvent::class, messagePublishService, messageSubscribeService) {
    override fun handle(event: Mono<IpAssignedEvent>) = event.map{ it.toDto() }.flatMap { ipAssignedEventHandler.handle(it, it) }
}