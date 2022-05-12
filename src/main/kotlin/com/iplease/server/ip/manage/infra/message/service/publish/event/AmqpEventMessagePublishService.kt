package com.iplease.server.ip.manage.infra.message.service.publish.event

import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.service.sender.AmqpMessageSender
import org.springframework.stereotype.Service

@Service
class AmqpEventMessagePublishService(
    private val amqpMessageSender: AmqpMessageSender
): EventMessagePublishService {
    override fun <T: Any> publish(key: Event, value: T): T = amqpMessageSender.send(key.routingKey, value)
}