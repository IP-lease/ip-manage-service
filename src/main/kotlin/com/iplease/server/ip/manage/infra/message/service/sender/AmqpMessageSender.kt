package com.iplease.server.ip.manage.infra.message.service.sender

interface AmqpMessageSender {
    fun <T: Any> send(routingKey: String, data: T): T
}