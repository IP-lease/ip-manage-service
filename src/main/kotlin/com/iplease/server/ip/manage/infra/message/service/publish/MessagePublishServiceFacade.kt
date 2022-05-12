package com.iplease.server.ip.manage.infra.message.service.publish

import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.data.type.Event

interface MessagePublishServiceFacade {
    fun <T: Any> publishEvent(key: Event, value: T): T
    fun <T: Any> publishError(key: Error, value: T): T
}