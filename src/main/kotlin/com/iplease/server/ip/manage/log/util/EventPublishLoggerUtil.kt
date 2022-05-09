package com.iplease.server.ip.manage.log.util

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EventPublishLoggerUtil: SimpleLoggerUtil<EventPublishInput, Any>(
    LoggerFactory.getLogger(EventPublishLoggerUtil::class.java),
    "[AMQP] [이벤트 - 발행]"
) {
    override fun logOnStart(input: EventPublishInput, uuid: String) {
        log("이벤트발행을 진행합니다.", uuid, true) { info(it) }
        log("이벤트 정보: $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: Any, uuid: String) {
        log("이벤트발행을 완료하였습니다.", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("이벤트발행중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
data class EventPublishInput(val routingKey: String, val data: Any)
