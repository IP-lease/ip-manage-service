package com.iplease.server.ip.manage.infra.event.service

import com.iplease.server.ip.manage.infra.event.listener.EventListener

interface EventSubscribeService {
    fun addListener(listener: EventListener)
}