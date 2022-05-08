package com.iplease.server.ip.manage.event.service

import com.iplease.server.ip.manage.event.listener.EventListener

interface EventSubscribeService {
    fun addListener(listener: EventListener)
}