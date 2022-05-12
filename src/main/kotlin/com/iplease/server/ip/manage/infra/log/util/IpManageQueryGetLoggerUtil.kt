package com.iplease.server.ip.manage.infra.log.util

import com.google.protobuf.Int64Value
import com.iplease.lib.ip.manage.AssignedIp
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpManageQueryGetLoggerUtil: LoggerUtil<Mono<Int64Value>, AssignedIp> {
    override fun logOnStart(input: Mono<Int64Value>, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnComplete(output: AssignedIp, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        TODO("Not yet implemented")
    }
}
