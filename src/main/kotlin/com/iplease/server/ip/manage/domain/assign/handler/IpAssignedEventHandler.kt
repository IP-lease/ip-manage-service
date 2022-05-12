package com.iplease.server.ip.manage.domain.assign.handler

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import reactor.core.publisher.Mono

interface IpAssignedEventHandler {
    fun handle(dto: AssignedIpDto) {
        onStart(dto)
            .doOnError{ onError(dto, it) }
            .doOnSuccess { onComplete(dto, it) }
            .onErrorReturn(dto)
            .block()
    }

    fun onStart(dto: AssignedIpDto): Mono<AssignedIpDto>
    fun onError(dto: AssignedIpDto, error: Throwable)
    fun onComplete(request: AssignedIpDto, response: AssignedIpDto)
}
