package com.iplease.server.ip.manage.infra.log.util

import com.iplease.server.ip.manage.global.common.data.dto.ReleasedIpDto
import org.springframework.stereotype.Component

@Component
class IpReleasedEventHandlerLoggerUtil: LoggerUtil<ReleasedIpDto, Unit> {
    override fun logOnStart(input: ReleasedIpDto, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnComplete(output: Unit, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        TODO("Not yet implemented")
    }

}
