package com.iplease.server.ip.manage.infra.message.data.dto

import com.iplease.server.ip.manage.infra.message.data.ErrorData

data class IpReleasedError(
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val throwable: Throwable
): ErrorData