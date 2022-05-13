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

    //성공적으로 Service 단에 할당해제로직을 위임하는지 테스트한다.
    @Test @DisplayName("이벤트 구독 - 구독 및 위임 성공")
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

    //만약 메세지의 페이로드(EventData) 가 올바르지 않을 경우, IpReleaseError 를 전파하는지 테스트한다
    @Test @DisplayName("이벤트 구독 - 잘못된 이벤트를 구독하였을 경우")
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