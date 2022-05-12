package com.iplease.server.ip.manage.infra.log.util

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.infra.message.data.dto.IpAssignedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpAssignedMessageSubscriberLoggerUtil: SimpleLoggerUtil<Mono<IpAssignedEvent>, AssignedIpDto>(
    LoggerFactory.getLogger(IpAssignedMessageSubscriberLoggerUtil::class.java),
    "[AMQP] [이벤트 - 구독]"
) {
    override fun logOnStart(input: Mono<IpAssignedEvent>, uuid: String) {
        log("IP 할당 이벤트 구독을 진행합니다.", uuid, true) { info(it)}
    }

    override fun logOnComplete(output: AssignedIpDto, uuid: String) {
        log("IP 할당 이벤트 구독을 완료하였습니다.", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("IP 할당 이벤트 구독중 오류가 발생하였습니다.", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
