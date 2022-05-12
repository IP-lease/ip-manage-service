package com.iplease.server.ip.manage.infra.message.service.publish.error

import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.service.sender.AmqpMessageSender
import org.springframework.stereotype.Service

@Service
class AmqpErrorMessagePublishService(
    private val amqpMessageSender: AmqpMessageSender
): ErrorMessagePublishService {
    override fun <T: Any> publish(key: Error, value: T): T = amqpMessageSender.send(key.routingKey, value)
}