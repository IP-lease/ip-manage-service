package com.iplease.server.ip.manage.infra.message.data.dto

import com.iplease.server.ip.manage.infra.message.data.ErrorData
import com.iplease.server.ip.manage.infra.message.data.type.Event

data class WrongPayloadError(
    private val originEvent: Event,
    val payload: String
): ErrorData {
    init { val originRoutingKey = originEvent.routingKey }
}
