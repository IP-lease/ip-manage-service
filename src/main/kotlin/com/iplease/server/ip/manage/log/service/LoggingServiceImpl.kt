package com.iplease.server.ip.manage.log.service

import com.iplease.server.ip.manage.log.type.LoggingActType
import com.iplease.server.ip.manage.log.util.EventPublishLoggerUtil
import com.iplease.server.ip.manage.log.util.EventSubscribeLoggerUtil
import com.iplease.server.ip.manage.log.util.LoggerUtil
import org.springframework.stereotype.Service

@Service
class LoggingServiceImpl(
    private val eventPublishLoggerUtil: EventPublishLoggerUtil,
    private val eventSubscribeLoggerUtil: EventSubscribeLoggerUtil,
): LoggingService {
    override fun <IN, OUT> getLoggerUtil(type: LoggingActType): LoggerUtil<IN, OUT> =
        when (type) {
            LoggingActType.EVENT_PUBLISH_LOGGER -> eventPublishLoggerUtil
            LoggingActType.EVENT_SUBSCRIBE_LOGGER -> eventSubscribeLoggerUtil
        } as LoggerUtil<IN, OUT>
}