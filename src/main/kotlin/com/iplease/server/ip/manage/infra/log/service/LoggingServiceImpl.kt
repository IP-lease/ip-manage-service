package com.iplease.server.ip.manage.infra.log.service

import com.iplease.server.ip.manage.infra.log.type.LoggerType
import com.iplease.server.ip.manage.infra.log.util.*
import org.springframework.stereotype.Service

@Service
class LoggingServiceImpl(
    private val eventPublishLoggerUtil: EventPublishLoggerUtil,
    private val eventSubscribeLoggerUtil: EventSubscribeLoggerUtil,
    private val ipManageQueryServiceGetLoggerUtil : IpManageQueryServiceGetLoggerUtil,
    private val ipManageServiceQueryExistLoggerUtil: IpManageServiceQueryExistLoggerUtil,
    private val ipAssignedMessageSubscriberLoggerUtil: IpAssignedMessageSubscriberLoggerUtil,
    private val ipAssignedEventHandlerLoggerUtil: IpAssignedEventHandlerLoggerUtil,
    private val ipAssignServiceLoggerUtil: IpAssignServiceLoggerUtil,
    private val ipReleasedMessageSubscriberLoggerUtil: IpReleasedMessageSubscriberLoggerUtil,
    private val ipReleasedEventHandlerLoggerUtil: IpReleasedEventHandlerLoggerUtil,
    private val ipReleaseServiceLoggerUtil: IpReleaseServiceLoggerUtil,
    private val ipReleaseReserveServiceLoggerUtil: IpReleaseReserveServiceLoggerUtil
): LoggingService {
    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    override fun <IN, OUT> getLoggerUtil(type: LoggerType): LoggerUtil<IN, OUT> =
        when (type) {
            //EVENT
            LoggerType.EVENT_PUBLISH_LOGGER -> eventPublishLoggerUtil
            LoggerType.EVENT_SUBSCRIBE_LOGGER -> eventSubscribeLoggerUtil
            //IP_MANAGE_QUERY
            LoggerType.IP_MANAGE_QUERY_GET_LOGGER -> ipManageQueryServiceGetLoggerUtil
            LoggerType.IP_MANAGE_QUERY_EXIST_LOGGER -> ipManageServiceQueryExistLoggerUtil
            //IP_ASSIGN
            LoggerType.IP_ASSIGNED_MESSAGE_SUBSCRIBER_LOGGER -> ipAssignedMessageSubscriberLoggerUtil
            LoggerType.IP_ASSIGNED_EVENT_HANDLER_LOGGER -> ipAssignedEventHandlerLoggerUtil
            LoggerType.IP_ASSIGN_SERVICE_LOGGER -> ipAssignServiceLoggerUtil
            //IP_RELEASE
            LoggerType.IP_RELEASED_MESSAGE_SUBSCRIBER_LOGGER -> ipReleasedMessageSubscriberLoggerUtil
            LoggerType.IP_RELEASED_EVENT_HANDLER_LOGGER -> ipReleasedEventHandlerLoggerUtil
            LoggerType.IP_RELEASE_SERVICE_LOGGER -> ipReleaseServiceLoggerUtil
            LoggerType.IP_RELEASE_RESERVE_SERVICE_LOGGER -> ipReleaseReserveServiceLoggerUtil
        } as LoggerUtil<IN, OUT>
}