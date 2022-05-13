package com.iplease.server.ip.manage.domain.release.service

import com.iplease.server.ip.manage.domain.release.exception.UnknownAssignedIpException
import com.iplease.server.ip.manage.global.common.repository.AssignedIpRepository
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.log.type.LoggerType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class IpReleaseServiceImpl(
    private val ipReleaseRepository: AssignedIpRepository,
    private val loggingService: LoggingService
): IpReleaseService {
    override fun release(uuid: Long): Mono<Unit> =
        ipReleaseRepository.existsById(uuid)
            .flatMap {
                if(it) ipReleaseRepository.deleteById(uuid)
                else Mono.defer { Mono.error(UnknownAssignedIpException(uuid)) }
            }.let { loggingService.withLog(uuid, it, LoggerType.IP_RELEASE_SERVICE_LOGGER) }
            .then(Unit.toMono())
}
