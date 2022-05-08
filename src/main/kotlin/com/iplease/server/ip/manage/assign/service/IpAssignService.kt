package com.iplease.server.ip.manage.assign.service

import com.iplease.server.ip.manage.assign.dto.AssignedIpDto
import reactor.core.publisher.Mono

interface IpAssignService {
    fun assign(assignedIpDto: AssignedIpDto): Mono<AssignedIpDto>
}
