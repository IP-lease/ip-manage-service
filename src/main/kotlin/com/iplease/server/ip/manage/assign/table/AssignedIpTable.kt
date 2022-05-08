package com.iplease.server.ip.manage.assign.table

import java.time.LocalDate

data class AssignedIpTable (
    val uuid: Long,
    val issuerUuid: Long,
    val assignerUuid: Long,
    val assignedAt: LocalDate,
    val expireAt: LocalDate
)