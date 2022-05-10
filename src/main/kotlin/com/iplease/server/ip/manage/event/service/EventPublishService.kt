package com.iplease.server.ip.manage.event.service

interface EventPublishService {
    fun <T: Any> publish(routingKey: String, data: T): T
}
