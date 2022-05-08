package com.iplease.server.ip.manage.assign.service

import com.iplease.server.ip.manage.assign.dto.AssignedIpDto
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
            .let { AssignedIpTable(it.uuid, it.issuerUuid, it.assignerUuid, it.assignedAt, it.expireAt) }
            .let { assignedIpRepository.save(it) }
            .map { AssignedIpDto(it.uuid, it.issuerUuid, it.assignerUuid, it.assignedAt, it.expireAt) }
}