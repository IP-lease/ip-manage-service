package com.iplease.server.ip.manage.domain.assign.service

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import reactor.core.publisher.Mono

interface IpAssignService {
    fun assign(assignedIpDto: AssignedIpDto): Mono<AssignedIpDto>
}
