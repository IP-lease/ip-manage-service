package com.iplease.server.ip.manage.infra.log.util

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class IpReleaseServiceLoggerUtil: SimpleLoggerUtil<Long, Void>(
    LoggerFactory.getLogger(IpReleaseServiceLoggerUtil::class.java),
    "[SERV] [IP할당 - 해제]"
) {
    override fun logOnStart(input: Long, uuid: String) {
        log("IP 할당해제를 진행합니다.", uuid, true) { info(it) }
        log("할당IP UUID 정보: $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: Void, uuid: String) {
        log("IP 할당해제를 완료하였습니다.", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("IP 할당해제중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}