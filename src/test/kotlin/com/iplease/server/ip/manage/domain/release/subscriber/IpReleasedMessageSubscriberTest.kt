package com.iplease.server.ip.manage.domain.release.subscriber

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.iplease.server.ip.manage.domain.release.handler.IpReleaseEventHandler
import com.iplease.server.ip.manage.global.common.data.dto.ReleasedIpDto
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.service.subscribe.MessageSubscribeService
import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedEvent
import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishServiceFacade
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleasedMessageSubscriberTest {
    private lateinit var ipReleaseEventHandler: IpReleaseEventHandler
    private lateinit var loggingService: LoggingService
    private lateinit var messagePublishService: MessagePublishServiceFacade
    private lateinit var messageSubscribeService: MessageSubscribeService
    private lateinit var target: IpReleasedMessageSubscriber

    private var assignedIpUuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()

    private lateinit var event: IpReleasedEvent
    private lateinit var eventByte: ByteArray
    private lateinit var releasedIpDto: ReleasedIpDto

    private lateinit var messageProperties: MessageProperties
    private lateinit var message: Message

    @BeforeEach
    fun setUp() {
        assignedIpUuid = Random.nextLong()
        issuerUuid = Random.nextLong()
        event = IpReleasedEvent(assignedIpUuid, issuerUuid)
        eventByte = ObjectMapper()
            .registerKotlinModule()
            .writeValueAsString(event)
            .toByteArray()
        releasedIpDto = ReleasedIpDto(assignedIpUuid, issuerUuid)
        ipReleaseEventHandler = mock()
        loggingService = mock()
        messagePublishService = mock()
        messageSubscribeService = mock()
        target = IpReleasedMessageSubscriber(ipReleaseEventHandler, loggingService, messagePublishService, messageSubscribeService)

        message = mock()
        messageProperties = mock()
    }

    //??????????????? Service ?????? ????????????????????? ??????????????? ???????????????.
    @Test @DisplayName("????????? ?????? - ?????? ??? ?????? ??????")
    fun subscribeSuccess() {
        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.IP_RELEASED.routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn(eventByte)
        whenever(ipReleaseEventHandler.handle(releasedIpDto, Unit)).thenReturn(Unit.toMono())
        whenever(loggingService.withLog(any<Mono<IpReleasedEvent>>(), any<Mono<Unit>>(), any())).thenAnswer { return@thenAnswer it.arguments[1] }

        target.subscribe(message)
        verify(ipReleaseEventHandler, times(1)).handle(releasedIpDto, Unit)
        verify(messagePublishService, never()).publishError(any(), any())
    }

    //?????? ???????????? ????????????(EventData) ??? ???????????? ?????? ??????, IpReleaseError ??? ??????????????? ???????????????
    @Test @DisplayName("????????? ?????? - ????????? ???????????? ??????????????? ??????")
    fun subscribeMalformedPayload() {
        val eventStr = "test${Random.nextLong()}"
        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.IP_RELEASED.routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn(eventStr.toByteArray())
        whenever(loggingService.withLog(any<Mono<IpReleasedEvent>>(), any<Mono<Unit>>(), any())).thenAnswer { return@thenAnswer (it.arguments[1])}

        target.subscribe(message)
        verify(ipReleaseEventHandler, never()).handle(any(), any())
        verify(messagePublishService, times(1)).publishError(eq(Error.WRONG_PAYLOAD), any())
    }
}