package com.iplease.server.ip.manage.infra.log.util

import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpReleasedMessageSubscriberLoggerUtil: SimpleLoggerUtil<Mono<IpReleasedEvent>, Unit>(
    LoggerFactory.getLogger(IpReleasedMessageSubscriberLoggerUtil::class.java),
    "[AMQP] [이벤트 - 구독]"
) {
    override fun logOnStart(input: Mono<IpReleasedEvent>, uuid: String) {
        log("IP 할당해제 이벤트 구독을 진행합니다.", uuid, true) { info(it) }
    }

    override fun logOnComplete(output: Unit, uuid: String) {
        log("IP 할당해제 이벤트 구독을 완료하였습니다.", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("IP 할당해제 이벤트 구독중 오류가 발생하였습니다.", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
