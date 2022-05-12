package com.iplease.server.ip.manage.infra.event.data.dto

import com.iplease.server.ip.manage.global.common.data.dto.ReleasedIpDto

data class IpReleasedEvent(
    val assignedIpUuid: Long,
    val issuerUuid: Long) {
    fun toDto() = ReleasedIpDto(assignedIpUuid, issuerUuid)
}
