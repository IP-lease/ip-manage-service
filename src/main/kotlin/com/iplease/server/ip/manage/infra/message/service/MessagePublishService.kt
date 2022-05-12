package com.iplease.server.ip.manage.infra.message.service

import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.data.type.Error

interface MessagePublishService {
    fun <T: Any> publish(routingKey: String, data: T): T
    fun <T: Any> publish(error: Error, data: T): T = publish(error.routingKey, data)
    fun <T: Any> publish(event: Event, data: T): T = publish(event.routingKey, data)
}
