package com.iplease.server.ip.manage.global.release

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import reactor.core.publisher.Mono

interface IpReleaseReserveService {
    fun reserve(dto: AssignedIpDto): Mono<AssignedIpDto>
}
