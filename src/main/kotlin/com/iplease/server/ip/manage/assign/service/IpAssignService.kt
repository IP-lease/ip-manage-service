package com.iplease.server.ip.manage.assign.service

import com.iplease.server.ip.manage.assign.data.dto.AssignedIpDto
import reactor.core.publisher.Mono

interface IpAssignService {
    fun assign(assignedIpDto: AssignedIpDto): Mono<AssignedIpDto>
}
