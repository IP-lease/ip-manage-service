package com.iplease.server.ip.manage.event.service

import com.iplease.server.ip.manage.event.listener.RabbitEventListener

interface EventSubscribeService {
    fun addListener(listener: RabbitEventListener)
}