package com.iplease.server.ip.manage.infra.log.util

import org.springframework.stereotype.Component

@Component
class IpReleaseServiceLoggerUtil: LoggerUtil<Long, Void> {
    override fun logOnStart(input: Long, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnComplete(output: Void, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        TODO("Not yet implemented")
    }

}
