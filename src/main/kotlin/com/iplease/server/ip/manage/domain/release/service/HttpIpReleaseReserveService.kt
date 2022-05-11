package com.iplease.server.ip.manage.domain.release.service

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.release.IpReleaseReserveService
import com.iplease.server.ip.release.global.common.data.type.Role
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class HttpIpReleaseReserveService(
    private val webClientBuilder: WebClient.Builder
): IpReleaseReserveService {
    override fun reserve(dto: AssignedIpDto): Mono<AssignedIpDto> =
        webClientBuilder
            .baseUrl("http://ip-release-server")
            .build()
            .post()
            .uri("/api/v1/ip/release/reserve/${dto.uuid}?releaseAt=${ DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dto.expireAt) }")
            .header("X-Login-Account-Uuid", dto.issuerUuid.toString())
            .header("X-Login-Account-Role", Role.OPERATOR.toString())
            .retrieve()
            .bodyToMono(IpReleaseReserveResponseDto::class.java)
            .map { dto }
            .onErrorReturn(dto)
}

data class IpReleaseReserveResponseDto (
    val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val releaseAt: LocalDate
)