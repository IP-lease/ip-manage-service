package com.iplease.server.ip.manage.infra.log.util

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.infra.message.data.dto.IpAssignedEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpAssignedMessageSubscriberLoggerUtil: LoggerUtil<Mono<IpAssignedEvent>, AssignedIpDto> {
    override fun logOnStart(input: Mono<IpAssignedEvent>, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnComplete(output: AssignedIpDto, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        TODO("Not yet implemented")
    }

}
