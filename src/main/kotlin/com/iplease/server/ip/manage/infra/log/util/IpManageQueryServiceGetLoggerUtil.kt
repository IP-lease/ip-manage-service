package com.iplease.server.ip.manage.infra.log.util

import com.google.protobuf.Int64Value
import com.iplease.lib.ip.manage.AssignedIp
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpManageQueryServiceGetLoggerUtil: SimpleLoggerUtil<Mono<Int64Value>, AssignedIp>(
    LoggerFactory.getLogger(IpManageQueryServiceGetLoggerUtil::class.java),
    "[GRPC] [IP관리 - 정보 조회]"
) {
    override fun logOnStart(input: Mono<Int64Value>, uuid: String) {
        log("할당IP 정보 조회를 진행합니다.", uuid, true) { info(it) }
    }

    override fun logOnComplete(output: AssignedIp, uuid: String) {
        log("할당IP 정보 조회를 완료하였습니다.", uuid) { info(it) }
        log("조회한 할당IP 정보: $output", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("할당IP 정보 조회중 오류가 발생하였습니다.", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
