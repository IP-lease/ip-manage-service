package com.iplease.server.ip.manage.infra.message.service

interface MessagePublishService {
    fun <T: Any> publish(routingKey: String, data: T): T
}
