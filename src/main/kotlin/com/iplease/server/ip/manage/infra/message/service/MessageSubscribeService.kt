package com.iplease.server.ip.manage.infra.message.service

import com.iplease.server.ip.manage.infra.message.listener.MessageListener

interface MessageSubscribeService {
    fun addListener(listener: MessageListener)
}