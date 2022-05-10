package com.iplease.server.ip.manage.infra.log.util

import com.iplease.server.ip.manage.global.assign.data.dto.AssignedIpDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class IpAssignLoggerUtil: SimpleLoggerUtil<AssignedIpDto, AssignedIpDto>(
    LoggerFactory.getLogger(IpAssignLoggerUtil::class.java),
    "[SERV] [IP 할당 - 등록]"
) {
    override fun logOnStart(input: AssignedIpDto, uuid: String) {
        log("IP 할당 등록을 진행합니다.", uuid, true) { info(it) }
        log("요청 정보: $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: AssignedIpDto, uuid: String) {
        log("IP 할당 등록을 완료하였습니다.", uuid, true) { info(it) }
        log("등록 정보: $output", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("IP 할당 등록중 오류가 발생하였습니다.", uuid, true) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid, true) { warn(it) }
    }
}