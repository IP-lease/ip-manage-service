package com.iplease.server.ip.manage.infra.message.data.dto

data class IpReleasedError(
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val throwable: Throwable
)
