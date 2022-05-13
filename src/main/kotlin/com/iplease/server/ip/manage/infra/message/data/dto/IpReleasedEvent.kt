package com.iplease.server.ip.manage.infra.message.data.dto

import com.iplease.server.ip.manage.global.common.data.dto.ReleasedIpDto
import com.iplease.server.ip.manage.infra.message.data.EventData

data class IpReleasedEvent(
    val assignedIpUuid: Long,
    val issuerUuid: Long): EventData {
    fun toDto() = ReleasedIpDto(assignedIpUuid, issuerUuid)
}
