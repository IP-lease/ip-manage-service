package com.iplease.server.ip.manage.infra.message.data.dto

import com.iplease.server.ip.manage.infra.message.data.ErrorData
import java.time.LocalDate

data class IpAssignedError(
    val issuerUuid: Long,
    val assignerUuid: Long,
    val assignedAt: LocalDate,
    val expireAt: LocalDate,
    val ipFirst: Int,
    val ipSecond: Int,
    val ipThird: Int,
    val ipFourth: Int,
    val throwable: Throwable
): ErrorData
