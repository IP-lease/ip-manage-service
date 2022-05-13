package com.iplease.server.ip.manage.infra.log.util

import com.google.protobuf.BoolValue
import com.google.protobuf.Int64Value
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpManageServiceQueryExistLoggerUtil: SimpleLoggerUtil<Mono<Int64Value>, BoolValue>(
    LoggerFactory.getLogger(IpManageServiceQueryExistLoggerUtil::class.java),
    "[GRPC] [IP관리 - 존재여부 조회]"
) {
    override fun logOnStart(input: Mono<Int64Value>, uuid: String) {
        log("할당IP 존재여부 조회를 진행합니다.", uuid, true) { info(it) }
    }

    override fun logOnComplete(output: BoolValue, uuid: String) {
        log("할당IP 존재여부 조회를 완료하였습니다.", uuid) { info(it) }
        log("조회한 할당IP 존재여부: ${output.value}", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("할당IP 존재여부 조회중 오류가 발생하였습니다.", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
