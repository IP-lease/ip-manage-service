package com.iplease.server.ip.manage.infra.log.service

import com.iplease.server.ip.manage.infra.log.type.LoggerType
import com.iplease.server.ip.manage.infra.log.util.EventPublishLoggerUtil
import com.iplease.server.ip.manage.infra.log.util.EventSubscribeLoggerUtil
import com.iplease.server.ip.manage.infra.log.util.IpAssignLoggerUtil
import com.iplease.server.ip.manage.infra.log.util.LoggerUtil
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
            LoggerType.IP_ASSIGN_SERVICE_LOGGER -> ipAssignLoggerUtil
        } as LoggerUtil<IN, OUT>
}