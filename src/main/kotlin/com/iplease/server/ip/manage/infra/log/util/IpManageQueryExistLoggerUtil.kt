package com.iplease.server.ip.manage.infra.log.util

import com.google.protobuf.BoolValue
import com.google.protobuf.Int64Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpManageQueryExistLoggerUtil: LoggerUtil<Mono<Int64Value>, BoolValue> {
    override fun logOnStart(input: Mono<Int64Value>, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnComplete(output: BoolValue, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        TODO("Not yet implemented")
    }
}
