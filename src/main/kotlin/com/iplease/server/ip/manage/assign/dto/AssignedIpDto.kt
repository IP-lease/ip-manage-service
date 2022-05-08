package com.iplease.server.ip.manage.assign.dto

import java.time.LocalDate

data class AssignedIpDto(
    val uuid: Long,
    val issuerUuid: Long,
    val assignerUuid: Long,
    val assignedAt: LocalDate,
    val expireAt: LocalDate
)
