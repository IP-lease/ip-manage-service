package com.iplease.server.ip.manage.infra.log.util

import com.iplease.server.ip.manage.global.common.data.dto.ReleasedIpDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class IpReleasedEventHandlerLoggerUtil: SimpleLoggerUtil<ReleasedIpDto, Unit>(
    LoggerFactory.getLogger(IpReleasedEventHandlerLoggerUtil::class.java),
    "[EVENT] [이벤트 - 처리]"
) {
    override fun logOnStart(input: ReleasedIpDto, uuid: String) {
        log("IP 할당해제 이벤트에 대한 처리를 진행합니다.", uuid, true) { info(it) }
        log("할당 해제 정보: $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: Unit, uuid: String) {
        log("IP 할당해제 이벤트에 대한 처리를 완료하였습니다.", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("IP 할당해제 이벤트에 대한 처리중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { info(it) }
    }
}
