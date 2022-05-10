package com.iplease.server.ip.manage.release.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.iplease.server.ip.manage.domain.release.listener.IpReleasedEventListener
import com.iplease.server.ip.manage.domain.release.service.IpReleaseService
import com.iplease.server.ip.manage.infra.event.data.dto.IpReleasedError
import com.iplease.server.ip.manage.infra.event.data.type.Event
import com.iplease.server.ip.manage.infra.event.service.EventPublishService
import com.iplease.server.ip.manage.infra.event.service.EventSubscribeService
import com.iplease.server.ip.manage.infra.event.data.dto.IpReleasedEvent
import com.iplease.server.ip.manage.infra.event.data.type.Error
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

class IpReleasedEventListenerTest {
    private lateinit var ipReleaseService: IpReleaseService
    private lateinit var eventPublishService: EventPublishService
    private lateinit var eventSubscribeService: EventSubscribeService
    private lateinit var target: IpReleasedEventListener
    private lateinit var messageProperties: MessageProperties
    private lateinit var message: Message
    private var assignedIpUuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()
    private lateinit var event: IpReleasedEvent
    private lateinit var eventByte: ByteArray

    @BeforeEach
    fun setUp() {
        assignedIpUuid = Random.nextLong()
        issuerUuid = Random.nextLong()
        event = IpReleasedEvent(assignedIpUuid, issuerUuid)
        eventByte = ObjectMapper()
            .registerKotlinModule()
            .writeValueAsString(event)
            .toByteArray()
        ipReleaseService = mock()
        eventPublishService = mock()
        eventSubscribeService = mock()
        target = IpReleasedEventListener(ipReleaseService, eventPublishService, eventSubscribeService)

        messageProperties = mock()
        message = mock()
    }

    //성공적으로 Service 단에 할당해제로직을 위임하는지 테스트한다.
    @Test @DisplayName("이벤트 구독 - 구독 및 위임 성공")
    fun subscribeSuccess() {
        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.IP_RELEASED.routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn(eventByte)
        whenever(ipReleaseService.release(assignedIpUuid)).thenReturn(Unit.toMono())

        target.handle(message)
        verify(ipReleaseService, times(1)).release(assignedIpUuid)
        verify(eventPublishService, times(0)).publish(any(), any())
    }

    //만약 Service 단에서 에외 발생시 IpReleaseError 를 전파하는지 테스트한다
    @Test @DisplayName("이벤트 구독 - 위임한 로직에서 예외가 발생할 경우")
    fun subscribeExceptionOccurred() {
        val throwable = java.lang.RuntimeException(Random.nextLong().toString())
        val error = IpReleasedError(assignedIpUuid, issuerUuid, throwable)

        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.IP_RELEASED.routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn(eventByte)
        whenever(ipReleaseService.release(assignedIpUuid)).thenReturn(Mono.error(throwable))

        target.handle(message)
        verify(ipReleaseService, times(1)).release(assignedIpUuid)
        verify(eventPublishService, times(1)).publish(Error.IP_RELEASED.routingKey, error)
    }

    //RoutingKey 가 IpRelease 가 아닐경우 어떠한 처리없이 로직을 종료하는지 테스트한다.
    @Test @DisplayName("이벤트 구독 - 이벤트가 구독 대상이 아닐경우")
    fun subscribeUnsupportedRoutingKey() {
        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.values().filter{ it != Event.IP_RELEASED }.random().routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn(eventByte)
        whenever(ipReleaseService.release(assignedIpUuid)).thenReturn(Unit.toMono())

        target.handle(message)
        verify(ipReleaseService, times(0)).release(any())
        verify(eventPublishService, times(0)).publish(any(), any())
    }

    //만약 메세지의 페이로드(EventData) 가 올바르지 않을 경우, IpReleaseError 를 전파하는지 테스트한다
    @Test @DisplayName("이벤트 구독 - 잘못된 이벤트를 구독하였을 경우")
    fun subscribeWrongPayload() {
        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.IP_RELEASED.routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn("test${Random.nextLong()}".toByteArray())
        whenever(ipReleaseService.release(assignedIpUuid)).thenReturn(Unit.toMono())

        target.handle(message)
        verify(ipReleaseService, times(0)).release(any())
        verify(eventPublishService, times(1)).publish(eq(Error.IP_RELEASED.routingKey), any())
    }
}