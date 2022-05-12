package com.iplease.server.ip.manage.infra.message.service

import com.iplease.server.ip.manage.infra.message.listener.MessageListener
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class RabbitMqMessageSubscribeService: MessageSubscribeService, MessageListener {
    private val list = mutableSetOf<MessageListener>()

    @RabbitListener(queues = ["server.ip.manage"])
    override fun handle(message: Message) = list.forEach { it.handle(message) }
    override fun addListener(listener: MessageListener) = list.add(listener).let { }
}