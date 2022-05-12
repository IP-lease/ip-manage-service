package com.iplease.server.ip.manage.domain.release.handler

import com.iplease.server.ip.manage.domain.release.service.IpReleaseService
import com.iplease.server.ip.manage.global.common.data.dto.ReleasedIpDto
import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedError
import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.service.MessagePublishService
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class IpReleaseEventHandlerImpl(
    private val messagePublishService: MessagePublishService,
    private val ipReleaseService: IpReleaseService
): IpReleaseEventHandler {
    override fun onStart(dto: ReleasedIpDto) = dto.toMono().flatMap { ipReleaseService.release(it.assignedIpUuid) }
    override fun onError(dto: ReleasedIpDto, error: Throwable) { messagePublishService.publish(Error.IP_RELEASED, dto.error(error)) }
    override fun onComplete(request: ReleasedIpDto, response: Unit) {}

    private fun ReleasedIpDto.error(throwable: Throwable) = IpReleasedError(assignedIpUuid, issuerUuid, throwable)
}