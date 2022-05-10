package com.iplease.server.ip.manage.infra.event.data.dto

data class IpReleasedEvent(
    val assignedIpUuid: Long,
    val issuerUuid: Long)
