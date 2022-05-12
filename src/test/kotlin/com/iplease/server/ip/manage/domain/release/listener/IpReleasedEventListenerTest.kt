package com.iplease.server.ip.manage.domain.release.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.iplease.server.ip.manage.domain.release.handler.IpReleaseEventHandler
import com.iplease.server.ip.manage.global.common.data.dto.ReleasedIpDto
import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.service.EventPublishService
import com.iplease.server.ip.manage.infra.message.service.EventSubscribeService
import com.iplease.server.ip.manage.infra.message.data.dto.IpReleasedEvent
import com.iplease.server.ip.manage.infra.message.data.dto.WrongPayloadError
import com.iplease.server.ip.manage.infra.message.data.type.Error
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import reactor.kotlin.core.publisher.toMono
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleasedEventListenerTest {
    private lateinit var ipReleaseEventHandler: IpReleaseEventHandler
    private lateinit var eventPublishService: EventPublishService
    private lateinit var eventSubscribeService: EventSubscribeService
    private lateinit var target: IpReleasedEventListener

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
        eventPublishService = mock()
        eventSubscribeService = mock()
        target = IpReleasedEventListener(ipReleaseEventHandler, eventPublishService, eventSubscribeService)

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

        target.handle(message)
        verify(ipReleaseEventHandler, times(1)).handle(releasedIpDto, Unit)
        verify(eventPublishService, never()).publish(any(), any())
    }

    //RoutingKey 가 IpRelease 가 아닐경우 어떠한 처리없이 로직을 종료하는지 테스트한다.
    @Test @DisplayName("이벤트 구독 - 이벤트가 구독 대상이 아닐경우")
    fun subscribeUnsupportedRoutingKey() {
        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.values().filter{ it != Event.IP_RELEASED }.random().routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn(eventByte)

        target.handle(message)
        verify(ipReleaseEventHandler, never()).handle(any(), any())
        verify(eventPublishService, never()).publish(any(), any())
    }

    //만약 메세지의 페이로드(EventData) 가 올바르지 않을 경우, IpReleaseError 를 전파하는지 테스트한다
    @Test @DisplayName("이벤트 구독 - 잘못된 이벤트를 구독하였을 경우")
    fun subscribeMalformedPayload() {
        val eventStr = "test${Random.nextLong()}"
        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.IP_RELEASED.routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn(eventStr.toByteArray())

        target.handle(message)
        verify(ipReleaseEventHandler, never()).handle(any(), any())
        verify(eventPublishService, times(1)).publish(
            Error.WRONG_PAYLOAD.routingKey,
            WrongPayloadError(Event.IP_RELEASED, message.body.toString())
        )
    }
}