package com.iplease.server.ip.manage.assign.service

import com.iplease.server.ip.manage.assign.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.assign.data.dto.IpDto
import com.iplease.server.ip.manage.assign.repository.AssignedIpRepository
import com.iplease.server.ip.manage.assign.data.table.AssignedIpTable
import com.iplease.server.ip.manage.assign.exception.WrongExpireDateException
import com.iplease.server.ip.manage.assign.util.DateUtil
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class IpAssignServiceImpl(
    private val dateUtil: DateUtil,
    private val assignedIpRepository: AssignedIpRepository
) : IpAssignService {
    override fun assign(assignedIpDto: AssignedIpDto): Mono<AssignedIpDto> =
        checkExpireDate(assignedIpDto)
            .map{ assignedIpDto }
            .map { AssignedIpTable(0L, it.issuerUuid, it.assignerUuid, it.assignedAt, it.expireAt, it.ip.first, it.ip.second, it.ip.third, it.ip.fourth) }
            .flatMap { assignedIpRepository.save(it) }
            .map { AssignedIpDto(it.uuid, it.issuerUuid, it.assignerUuid, it.assignedAt, it.expireAt, IpDto(it.ipFirst, it.ipSecond, it.ipThird, it.ipFourth)) }

    private fun checkExpireDate(dto: AssignedIpDto): Mono<Any> =
        dto.toMono()
            .flatMap {
                if(!it.assignedAt.isBefore(it.expireAt)) Mono.defer { Mono.error(WrongExpireDateException(it.expireAt, "할당 만료일은 할당일 이후여야합니다!")) }
                else Mono.just(it)
            }.flatMap {
                if(!dto.assignedAt.isAfter(dateUtil.dateNow())) Mono.defer { Mono.error(WrongExpireDateException(dto.expireAt, "할당 만료일은 익일 이후여야 합니다!"))}
                else Mono.just(it)
            }
}