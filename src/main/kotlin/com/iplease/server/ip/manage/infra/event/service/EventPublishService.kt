package com.iplease.server.ip.manage.infra.event.service

interface EventPublishService {
    fun <T: Any> publish(routingKey: String, data: T): T
}
