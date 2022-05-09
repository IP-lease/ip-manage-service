package com.iplease.server.ip.manage.event.type

enum class Error(
    val routingKey: String
) {
    IP_ASSIGNED("v1.error.ip.demand.success"),
}