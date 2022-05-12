package com.iplease.server.ip.manage.domain.assign.subscriber

import com.iplease.server.ip.manage.domain.assign.handler.IpAssignedEventHandler
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.log.type.LoggerType
import com.iplease.server.ip.manage.infra.message.data.dto.IpAssignedEvent
import com.iplease.server.ip.manage.infra.message.service.subscribe.MessageSubscribeService
import com.iplease.server.ip.manage.infra.message.listener.EventMessageSubscriberV2
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishServiceFacade
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpAssignedMessageSubscriber(
    private val ipAssignedEventHandler: IpAssignedEventHandler,
    private val loggingService: LoggingService,
    messagePublishService: MessagePublishServiceFacade,
    messageSubscribeService: MessageSubscribeService
): EventMessageSubscriberV2<IpAssignedEvent>(IpAssignedEvent::class, messagePublishService, messageSubscribeService) {
    override fun handle(event: Mono<IpAssignedEvent>) = event
        .map { it.toDto() }
        .flatMap { ipAssignedEventHandler.handle(it, it) }
        .let { loggingService.withLog(event, it, LoggerType.IP_ASSIGNED_MESSAGE_SUBSCRIBER_LOGGER) }
}