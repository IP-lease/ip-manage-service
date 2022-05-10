package com.iplease.server.ip.manage.log.service

import com.iplease.server.ip.manage.log.type.LoggerType
import com.iplease.server.ip.manage.log.util.EventPublishLoggerUtil
import com.iplease.server.ip.manage.log.util.EventSubscribeLoggerUtil
import com.iplease.server.ip.manage.log.util.IpAssignLoggerUtil
import com.iplease.server.ip.manage.log.util.LoggerUtil
import org.springframework.stereotype.Service

@Service
class LoggingServiceImpl(
    private val eventPublishLoggerUtil: EventPublishLoggerUtil,
    private val eventSubscribeLoggerUtil: EventSubscribeLoggerUtil,
    private val ipAssignLoggerUtil: IpAssignLoggerUtil
): LoggingService {
    override fun <IN, OUT> getLoggerUtil(type: LoggerType): LoggerUtil<IN, OUT> =
        when (type) {
            LoggerType.EVENT_PUBLISH_LOGGER -> eventPublishLoggerUtil
            LoggerType.EVENT_SUBSCRIBE_LOGGER -> eventSubscribeLoggerUtil
            LoggerType.IP_ASSIGN_LOGGER -> ipAssignLoggerUtil
        } as LoggerUtil<IN, OUT>
}