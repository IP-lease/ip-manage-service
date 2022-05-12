package com.iplease.server.ip.manage.infra.log.util

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import org.springframework.stereotype.Component

@Component
class IpReleaseReserveLoggerUtil: LoggerUtil<AssignedIpDto, AssignedIpDto> {
    override fun logOnStart(input: AssignedIpDto, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnComplete(output: AssignedIpDto, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        TODO("Not yet implemented")
    }
}
