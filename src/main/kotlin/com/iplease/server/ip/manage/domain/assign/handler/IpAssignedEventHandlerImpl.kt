package com.iplease.server.ip.manage.domain.assign.handler

import com.iplease.server.ip.manage.domain.assign.service.IpAssignService
import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.release.IpReleaseReserveService
import com.iplease.server.ip.manage.infra.message.data.dto.IpAssignedError
import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.service.MessagePublishService
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class IpAssignedEventHandlerImpl(
    private val ipAssignService: IpAssignService,
    private val messagePublishService: MessagePublishService,
    private val ipReleaseReserveService: IpReleaseReserveService,
): IpAssignedEventHandler {
    override fun onStart(dto: AssignedIpDto) =
        dto.toMono()
            .flatMap { ipAssignService.assign(it) }
            .flatMap { ipReleaseReserveService.reserve(it) } //TODO 로직 처리중에 에러가 날경우 이를 전파해야할지 아니면 예약이 안된상태로 IP를 할당해야하는지 고민해보기

    override fun onError(dto: AssignedIpDto, error: Throwable) {
        messagePublishService.publish(Error.IP_ASSIGNED.routingKey, dto.error(error))
    }

    override fun onComplete(request: AssignedIpDto, response: AssignedIpDto) {}

    private fun AssignedIpDto.error(throwable: Throwable) = IpAssignedError(
        issuerUuid, assignerUuid, assignedAt, expireAt,
        ip.first, ip.second, ip.third, ip.fourth,
        throwable
    )
}