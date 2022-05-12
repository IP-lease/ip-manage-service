package com.iplease.server.ip.manage.infra.message.service

import com.iplease.server.ip.manage.infra.message.listener.MessageSubscriber
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class RabbitMqMessageSubscribeService: MessageSubscribeService, MessageSubscriber {
    private val list = mutableSetOf<MessageSubscriber>()

    @RabbitListener(queues = ["server.ip.manage"])
    override fun subscribe(message: Message) = list.forEach { it.subscribe(message) }
    override fun addListener(listener: MessageSubscriber) = list.add(listener).let { }
}