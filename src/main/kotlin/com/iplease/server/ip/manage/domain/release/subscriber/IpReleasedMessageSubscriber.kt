package com.iplease.server.ip.manage.domain.release.subscriber

import com.iplease.server.ip.manage.domain.release.handler.IpReleaseEventHandler
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.log.type.LoggerType
import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedEvent
import com.iplease.server.ip.manage.infra.message.listener.EventMessageSubscriberV2
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishServiceFacade
import com.iplease.server.ip.manage.infra.message.service.subscribe.MessageSubscribeService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpReleasedMessageSubscriber(
    private val ipReleaseEventHandler: IpReleaseEventHandler,
    private val loggingService: LoggingService,
    messagePublishService: MessagePublishServiceFacade,
    messageSubscribeService: MessageSubscribeService
): EventMessageSubscriberV2<IpReleasedEvent>(IpReleasedEvent::class, messagePublishService, messageSubscribeService) {
    override fun handle(event: Mono<IpReleasedEvent>) = event
        .map { it.toDto() }
        .flatMap { ipReleaseEventHandler.handle(it, Unit) }
        .let { loggingService.withLog(event, it, LoggerType.IP_RELEASED_MESSAGE_SUBSCRIBER_LOGGER) }
}
