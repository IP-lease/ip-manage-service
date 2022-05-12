package com.iplease.server.ip.manage.infra.message.service

import com.iplease.server.ip.manage.infra.message.listener.EventListener

interface EventSubscribeService {
    fun addListener(listener: EventListener)
}