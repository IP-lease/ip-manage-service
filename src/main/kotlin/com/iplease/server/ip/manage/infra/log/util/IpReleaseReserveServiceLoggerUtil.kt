package com.iplease.server.ip.manage.infra.log.util

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class IpReleaseReserveServiceLoggerUtil: SimpleLoggerUtil<AssignedIpDto, AssignedIpDto>(
    LoggerFactory.getLogger(IpReleaseReserveServiceLoggerUtil::class.java),
    "[SERV] [IP할당 - 해제예약]"
) {
    override fun logOnStart(input: AssignedIpDto, uuid: String) {
        log("IP 할당해제 예약을 진행합니다.", uuid, true) { info(it) }
        log("할당IP 정보 : $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: AssignedIpDto, uuid: String) {
        log("IP 할당해제 예약을 완료하였습니다.", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("IP 할당해제 예약중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}