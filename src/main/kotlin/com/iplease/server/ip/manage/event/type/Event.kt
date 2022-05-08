package com.iplease.server.ip.manage.event.type

enum class Event(
    val routingKey: String
) {
    IP_ASSIGNED("v1.event.ip.demand.success"),
    IP_RELEASED("v1.event.ip.release.released")
}
