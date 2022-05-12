package com.iplease.server.ip.manage.global.common.handler

import reactor.core.publisher.Mono

interface EventHandler<T: Any> {
    fun handle(dto: T): Mono<T> =
        onStart(dto)
            .doOnError{ onError(dto, it) }
            .doOnSuccess { onComplete(dto, it) }
            .onErrorReturn(dto)

    fun onStart(dto: T): Mono<T>
    fun onError(dto: T, error: Throwable)
    fun onComplete(request: T, response: T)
}