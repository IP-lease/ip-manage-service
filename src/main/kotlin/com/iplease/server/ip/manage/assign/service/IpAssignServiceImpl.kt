package com.iplease.server.ip.manage.assign.service

import com.iplease.server.ip.manage.assign.dto.AssignedIpDto
import com.iplease.server.ip.manage.assign.dto.IpDto
import com.iplease.server.ip.manage.assign.repository.AssignedIpRepository
import com.iplease.server.ip.manage.assign.table.AssignedIpTable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IpAssignServiceImpl(
    private val assignedIpRepository: AssignedIpRepository
) : IpAssignService {
    override fun assign(assignedIpDto: AssignedIpDto): Mono<AssignedIpDto> =
        assignedIpDto
            .run { AssignedIpTable(0L, issuerUuid, assignerUuid, assignedAt, expireAt, ip.first, ip.second, ip.third, ip.fourth) }
            .let { assignedIpRepository.save(it) }
            .map { AssignedIpDto(it.uuid, it.issuerUuid, it.assignerUuid, it.assignedAt, it.expireAt, IpDto(it.ipFirst, it.ipSecond, it.ipThird, it.ipFourth)) }
}