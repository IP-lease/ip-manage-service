package com.iplease.server.ip.manage.event.dto

import com.iplease.server.ip.manage.assign.dto.AssignedIpDto
import com.iplease.server.ip.manage.assign.dto.IpDto
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
) {
    fun toDto() = AssignedIpDto(
        0L, issuerUuid, assignerUuid, assignedAt, expireAt, 
        IpDto(ipFirst, ipSecond, ipThird, ipFourth))
}