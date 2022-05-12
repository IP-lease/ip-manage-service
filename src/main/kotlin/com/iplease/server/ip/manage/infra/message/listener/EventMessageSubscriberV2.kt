package com.iplease.server.ip.manage.infra.message.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.infra.message.data.dto.WrongPayloadError
import org.springframework.amqp.core.Message
import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishServiceFacade
import com.iplease.server.ip.manage.infra.message.service.subscribe.MessageSubscribeService
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.reflect.KClass
import kotlin.reflect.cast

abstract class EventMessageSubscriberV2<T: Any> (
    val type: KClass<T>,
    private val messagePublishService: MessagePublishServiceFacade,
    messageSubscribeService: MessageSubscribeService
): MessageSubscriber {
    private val event = Event.of(type)!!
    init { messageSubscribeService.addListener(this) }

    private fun parse(message: Message) =
        ObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(JavaTimeModule())
        .toMono()
        .map{ it.readValue(message.body, type.java) }
        .onErrorContinue {_, _ ->
            messagePublishService.publishError(Error.WRONG_PAYLOAD, WrongPayloadError(event, message.body.toString()))
        }

    override fun subscribe(message: Message) {
        if(message.messageProperties.receivedRoutingKey != event.routingKey) return
        parse(message)
            .map { type.cast(it) }
            .let { handle(it) }
            .block()
    }

    abstract fun handle(event: Mono<T>): Mono<*>
}