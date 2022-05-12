package com.iplease.server.ip.manage.infra.message.listener

import org.springframework.amqp.core.Message

interface EventListener {
    fun handle(message: Message)
}