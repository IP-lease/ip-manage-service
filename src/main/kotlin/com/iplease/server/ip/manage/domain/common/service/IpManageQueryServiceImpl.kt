package com.iplease.server.ip.manage.domain.common.service

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.common.data.dto.IpDto
import com.iplease.server.ip.manage.global.common.repository.AssignedIpRepository
import com.iplease.server.ip.manage.global.common.service.IpManageQueryService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IpManageQueryServiceImpl(
    val assignedIpRepository: AssignedIpRepository
    ) : IpManageQueryService {
    override fun existsAssignedIpByUuid(uuid: Long): Mono<Boolean> = assignedIpRepository.existsById(uuid)
    override fun getAssignedIpByUuid(uuid: Long): Mono<AssignedIpDto> =
        assignedIpRepository.findById(uuid)
            .map { AssignedIpDto(it.uuid, it.issuerUuid, it.assignerUuid, it.assignedAt, it.expireAt, IpDto(it.ipFirst, it.ipSecond, it.ipThird, it.ipFourth)) }
}