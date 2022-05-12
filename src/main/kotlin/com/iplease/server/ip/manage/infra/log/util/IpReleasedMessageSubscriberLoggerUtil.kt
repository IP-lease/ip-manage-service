package com.iplease.server.ip.manage.infra.log.util

import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IpReleasedMessageSubscriberLoggerUtil: LoggerUtil<Mono<IpReleasedEvent>, Unit> {
    override fun logOnStart(input: Mono<IpReleasedEvent>, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnComplete(output: Unit, uuid: String) {
        TODO("Not yet implemented")
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        TODO("Not yet implemented")
    }

}
