package com.iplease.server.ip.manage.infra.message.data.dto

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.common.data.dto.IpDto
import com.iplease.server.ip.manage.infra.message.data.EventData
import java.time.LocalDate

data class IpAssignedEvent (
    val issuerUuid: Long,
    val assignerUuid: Long,
    val assignedAt: LocalDate,
    val expireAt: LocalDate,
    val ipFirst: Int,
    val ipSecond: Int,
    val ipThird: Int,
    val ipFourth: Int
): EventData {
    fun toDto() = AssignedIpDto(
        0L, issuerUuid, assignerUuid, assignedAt, expireAt, 
        IpDto(ipFirst, ipSecond, ipThird, ipFourth)
    )
}