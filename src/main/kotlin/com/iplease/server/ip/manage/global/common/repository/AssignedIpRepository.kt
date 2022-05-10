package com.iplease.server.ip.manage.global.common.repository

import com.iplease.server.ip.manage.global.common.data.table.AssignedIpTable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface AssignedIpRepository: R2dbcRepository<AssignedIpTable, Long> {
    fun existsByIpFirstAndIpSecondAndIpThirdAndIpFourth(ipFirst: Int, ipSecond: Int, ipThird: Int, ipFourth: Int): Mono<Boolean>
}
