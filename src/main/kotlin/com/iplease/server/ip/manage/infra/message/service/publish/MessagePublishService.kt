package com.iplease.server.ip.manage.infra.message.service.publish

interface MessagePublishService<K> {
    fun <T: Any> publish(key: K, value: T): T
}