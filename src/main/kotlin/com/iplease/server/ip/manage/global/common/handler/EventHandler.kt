package com.iplease.server.ip.manage.global.common.handler

import reactor.core.publisher.Mono

interface EventHandler<T: Any, R: Any> {
    fun handle(dto: T, onErrorReturn: R): Mono<R> =
        onStart(dto)
            .doOnError{ onError(dto, it) }
            .doOnSuccess { onComplete(dto, it) }
            .onErrorReturn(onErrorReturn)

    fun onStart(dto: T): Mono<R>
    fun onError(dto: T, error: Throwable)
    fun onComplete(request: T, response: R)
}