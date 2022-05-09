package com.iplease.server.ip.manage.event.service

import com.iplease.server.ip.manage.event.listener.EventListener
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class RabbitMqEventSubscribeService: EventSubscribeService, EventListener {
    private val list = mutableSetOf<EventListener>()

    @RabbitListener(queues = ["server.ip.manage"])
    override fun handle(message: Message) = list.forEach { it.handle(message) }
    override fun addListener(listener: EventListener) = list.add(listener).let { }
}