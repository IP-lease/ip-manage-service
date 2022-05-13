package com.iplease.server.ip.manage.domain.release.service

import reactor.core.publisher.Mono

interface IpReleaseService {
    fun release(uuid: Long): Mono<Unit>
}
