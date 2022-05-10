package com.iplease.server.ip.manage.domain.release.service

import com.iplease.server.ip.manage.domain.release.exception.UnknownAssignedIpException
import com.iplease.server.ip.manage.global.common.repository.AssignedIpRepository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class IpReleaseServiceImpl(
    private val ipReleaseRepository: AssignedIpRepository
): IpReleaseService {
    override fun release(uuid: Long): Mono<Unit> =
        ipReleaseRepository.existsById(uuid)
            .flatMap {
                if(it) ipReleaseRepository.deleteById(uuid)
                else Mono.defer { Mono.error(UnknownAssignedIpException(uuid)) }
            }.then(Unit.toMono())
}
