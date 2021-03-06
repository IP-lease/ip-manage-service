package com.iplease.server.ip.manage.infra.message.data.type

enum class Error(
    val routingKey: String
) {
    IP_ASSIGNED("v1.error.ip.demand.success"),
    IP_RELEASED("v1.error.ip.release.released"),
    WRONG_PAYLOAD("v1.error.payload.wrong"),
}