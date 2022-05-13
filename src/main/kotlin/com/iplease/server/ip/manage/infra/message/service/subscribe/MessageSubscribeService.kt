package com.iplease.server.ip.manage.infra.message.service.subscribe

import com.iplease.server.ip.manage.infra.message.listener.MessageSubscriber

interface MessageSubscribeService {
    fun addListener(listener: MessageSubscriber)
}