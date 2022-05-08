package com.iplease.server.ip.manage.event.dto

import java.time.LocalDate

data class IpAssignedEvent (
    val issuerUuid: Long,
    val assignerUuid: Long,
    val assignedAt: LocalDate,
    val expireAt: LocalDate
)
