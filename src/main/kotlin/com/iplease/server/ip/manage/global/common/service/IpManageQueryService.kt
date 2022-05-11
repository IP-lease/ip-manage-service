package com.iplease.server.ip.manage.global.common.service

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import reactor.core.publisher.Mono

interface IpManageQueryService {
    fun existsAssignedIpByUuid(uuid: Long): Mono<Boolean>
    fun getAssignedIpByUuid(uuid: Long): Mono<AssignedIpDto>
}
