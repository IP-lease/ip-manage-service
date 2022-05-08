package com.iplease.server.ip.manage.event.service

import com.iplease.server.ip.manage.event.listener.EventListener
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class MqEventSubscribeService: EventSubscribeService, EventListener {
    private val list = mutableSetOf<EventListener>()

    @RabbitListener(queues = ["server.ip.manage"])
    override fun handle(@Payload event: String, message: Message) = list.forEach { it.handle(event, message) }
    override fun addListener(listener: EventListener) = list.add(listener).let { }
}