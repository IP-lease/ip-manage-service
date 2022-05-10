package com.iplease.server.ip.manage.log.service

import com.iplease.server.ip.manage.log.type.LoggerType
import com.iplease.server.ip.manage.log.util.LoggerUtil
import reactor.core.publisher.Mono
import java.nio.ByteBuffer
import java.util.*

interface LoggingService {
    fun <IN, OUT> withLog(input: IN, output: Mono<OUT>, type: LoggerType): Mono<OUT> =
        log(input, output, getLoggerUtil<IN, OUT>(type), UUID.randomUUID().toString())
    private fun <IN, OUT> log(input: IN, mono: Mono<OUT>, logger: LoggerUtil<IN, OUT>, rawUuid: String): Mono<OUT> =
        ByteBuffer.wrap(rawUuid.toByteArray()).long.toString().let {
            uuid ->
            mono.doOnSubscribe { logger.logOnStart(input, uuid) }
                .doOnSuccess { logger.logOnComplete(it, uuid) }
                .doOnError { logger.logOnError(it, uuid) }
        }

    fun <IN, OUT>getLoggerUtil(type: LoggerType): LoggerUtil<IN, OUT>
}