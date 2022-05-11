package com.iplease.server.ip.manage.domain.release.service

import com.iplease.server.ip.manage.domain.assign.util.DateUtil
import com.iplease.server.ip.manage.global.release.IpReleaseReserveService
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.release.global.common.data.type.Role
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class HttpIpReleaseReserveService(
    private val webClientBuilder: WebClient.Builder,
    private val loggingService: LoggingService,
    private val dateUtil: DateUtil
): IpReleaseReserveService {
    override fun reserve(uuid: Long, issuerUuid: Long, expireAt: LocalDate) {
        val releaseAt = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(expireAt)
        webClientBuilder
            .baseUrl("http://ip-release-server")
            .build()
            .post()
            .uri("/api/v1/ip/release/reserve/$uuid?releaseAt=$releaseAt")
            .header("X-Login-Account-Uuid", issuerUuid.toString())
            .header("X-Login-Account-Role", Role.OPERATOR.toString())
            .retrieve()
            .bodyToMono(IpReleaseReserveResponseDto::class.java)
            .onErrorReturn(IpReleaseReserveResponseDto(0L, 0L, 0L, dateUtil.dateNow()))
            .block()
    }
}

data class IpReleaseReserveResponseDto (
    val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val releaseAt: LocalDate
)