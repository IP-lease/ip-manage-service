package com.iplease.server.ip.manage.infra.message.service.publish

import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.service.publish.error.ErrorMessagePublishService
import com.iplease.server.ip.manage.infra.message.service.publish.event.EventMessagePublishService
import org.springframework.stereotype.Service

@Service
class MessagePublishServiceFacadeImpl(
    private val errorMessagePublishService: ErrorMessagePublishService,
    private val eventMessagePublishService: EventMessagePublishService
): MessagePublishServiceFacade {
    override fun <T : Any> publishEvent(key: Event, value: T): T = eventMessagePublishService.publish(key, value)
    override fun <T : Any> publishError(key: Error, value: T): T = errorMessagePublishService.publish(key, value)
}