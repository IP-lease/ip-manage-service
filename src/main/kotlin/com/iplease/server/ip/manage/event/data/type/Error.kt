package com.iplease.server.ip.manage.event.data.type

enum class Error(
    val routingKey: String
) {
    IP_ASSIGNED("v1.error.ip.demand.success"),
}