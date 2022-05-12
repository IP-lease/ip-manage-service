package com.iplease.server.ip.manage.infra.message.data.dto

import com.iplease.server.ip.manage.infra.message.data.type.Event

data class WrongPayloadError(
    private val originEvent: Event,
    val payload: String
) {
    init {
        val originRoutingKey = originEvent.routingKey
    }
}
