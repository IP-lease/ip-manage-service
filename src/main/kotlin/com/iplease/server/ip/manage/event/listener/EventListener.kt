package com.iplease.server.ip.manage.event.listener

import org.springframework.amqp.core.Message

interface EventListener {
    fun handle(event: String, message: Message)
}