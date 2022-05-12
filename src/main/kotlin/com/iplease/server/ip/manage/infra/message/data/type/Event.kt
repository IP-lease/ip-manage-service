package com.iplease.server.ip.manage.infra.message.data.type

import com.iplease.server.ip.manage.infra.message.data.dto.IpAssignedEvent
import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedEvent
import kotlin.reflect.KClass

enum class Event(
    val routingKey: String,
    val eventDataType: KClass<*>
) {
    IP_ASSIGNED("v1.event.ip.demand.success", IpAssignedEvent::class),
    IP_RELEASED("v1.event.ip.release.released", IpReleasedEvent::class);

    companion object {
        fun of(eventDataType: KClass<*>): Event? {
            return values().find { it.eventDataType == eventDataType }
        }
    }
}
