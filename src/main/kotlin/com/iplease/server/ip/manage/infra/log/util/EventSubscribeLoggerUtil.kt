package com.iplease.server.ip.manage.infra.log.util

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EventSubscribeLoggerUtil: SimpleLoggerUtil<EventSubscribeInput, Unit>(
    LoggerFactory.getLogger(EventPublishLoggerUtil::class.java),
    "[AMQP] [이벤트 - 구독]"
) {
    override fun logOnStart(input: EventSubscribeInput, uuid: String) {
        log("이벤트가 구독되었습니다.", uuid, true) { info(it) }
        log("이벤트 정보: $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: Unit, uuid: String) {}

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("이벤트구독중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
data class EventSubscribeInput(val routingKey: String, val data: Any)
