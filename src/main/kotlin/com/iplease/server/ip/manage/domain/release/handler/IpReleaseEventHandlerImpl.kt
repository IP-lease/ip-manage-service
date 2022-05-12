package com.iplease.server.ip.manage.domain.release.handler

import com.iplease.server.ip.manage.domain.release.service.IpReleaseService
import com.iplease.server.ip.manage.global.common.data.dto.ReleasedIpDto
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.log.type.LoggerType
import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedError
import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishServiceFacade
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class IpReleaseEventHandlerImpl(
    private val messagePublishService: MessagePublishServiceFacade,
    private val ipReleaseService: IpReleaseService,
    private val loggingService: LoggingService
): IpReleaseEventHandler {
    override fun onStart(dto: ReleasedIpDto) =
        dto.toMono()
            .flatMap { ipReleaseService.release(it.assignedIpUuid) }
            .let { loggingService.withLog(dto, it, LoggerType.IP_RELEASED_EVENT_HANDLER_LOGGER) }
    override fun onError(dto: ReleasedIpDto, error: Throwable) { messagePublishService.publishError(Error.IP_RELEASED, dto.error(error)) }
    override fun onComplete(request: ReleasedIpDto, response: Unit) {}

    private fun ReleasedIpDto.error(throwable: Throwable) = IpReleasedError(assignedIpUuid, issuerUuid, throwable)
}