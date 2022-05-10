package com.iplease.server.ip.manage.domain.assign.service

import com.iplease.server.ip.manage.global.assign.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.assign.data.dto.IpDto
import com.iplease.server.ip.manage.global.assign.repository.AssignedIpRepository
import com.iplease.server.ip.manage.global.assign.data.table.AssignedIpTable
import com.iplease.server.ip.manage.domain.assign.exception.AlreadyExistsAssignedIpException
import com.iplease.server.ip.manage.domain.assign.exception.WrongExpireDateException
import com.iplease.server.ip.manage.domain.assign.util.DateUtil
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.log.type.LoggerType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class IpAssignServiceImpl(
    private val dateUtil: DateUtil,
    private val assignedIpRepository: AssignedIpRepository,
    private val loggingService: LoggingService
) : IpAssignService {
    override fun assign(assignedIpDto: AssignedIpDto): Mono<AssignedIpDto> =
        checkExpireDate(assignedIpDto)
            .flatMap { checkIpAddress(assignedIpDto.ip) }
            .map{ assignedIpDto }
            .map { AssignedIpTable(0L, it.issuerUuid, it.assignerUuid, it.assignedAt, it.expireAt, it.ip.first, it.ip.second, it.ip.third, it.ip.fourth) }
            .flatMap { assignedIpRepository.save(it) }
            .map { AssignedIpDto(it.uuid, it.issuerUuid, it.assignerUuid, it.assignedAt, it.expireAt, IpDto(it.ipFirst, it.ipSecond, it.ipThird, it.ipFourth)) }
            .let { loggingService.withLog(assignedIpDto, it, LoggerType.IP_ASSIGN_LOGGER) }

    private fun checkIpAddress(dto: IpDto): Mono<Any> =
        existsByIp(dto)
            .flatMap {
                if(it) Mono.defer { Mono.error(AlreadyExistsAssignedIpException("${dto.first}.${dto.second}.${dto.third}.${dto.fourth}")) }
                else Mono.just(it)
            }

    private fun checkExpireDate(dto: AssignedIpDto): Mono<Any> =
        dto.toMono()
            .flatMap {
                if(!it.assignedAt.isBefore(it.expireAt)) Mono.defer { Mono.error(WrongExpireDateException(it.expireAt, "할당 만료일은 할당일 이후여야합니다!")) }
                else Mono.just(it)
            }.flatMap {
                if(!dto.expireAt.isAfter(dateUtil.dateNow())) Mono.defer { Mono.error(WrongExpireDateException(dto.expireAt, "할당 만료일은 익일 이후여야 합니다!"))}
                else Mono.just(it)
            }

    private fun existsByIp(ip: IpDto) = assignedIpRepository.existsByIpFirstAndIpSecondAndIpThirdAndIpFourth(ip.first, ip.second, ip.third, ip.fourth)
}