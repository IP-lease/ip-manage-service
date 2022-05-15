package com.iplease.server.ip.manage.domain.assign.subscriber

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.domain.assign.handler.IpAssignedEventHandler
import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.common.data.dto.IpDto
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.message.data.dto.IpAssignedEvent
import com.iplease.server.ip.manage.infra.message.data.dto.WrongPayloadError
import com.iplease.server.ip.manage.infra.message.service.subscribe.MessageSubscribeService
import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishServiceFacade
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.random.Random

class IpAssignedMessageSubscriberTest {
    private lateinit var ipAssignedEventHandler: IpAssignedEventHandler
    private lateinit var loggingService: LoggingService
    private lateinit var messageSubscribeService: MessageSubscribeService
    private lateinit var messagePublishService: MessagePublishServiceFacade
    private lateinit var target: IpAssignedMessageSubscriber

    private var assignedIpUuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()
    private var assignerUuid by Delegates.notNull<Long>()
    private lateinit var assignedAt: LocalDate
    private lateinit var expireAt: LocalDate
    private lateinit var ip: List<Int>

    private lateinit var ipDto: IpDto
    private lateinit var assignedIpDto: AssignedIpDto
    private lateinit var event: IpAssignedEvent

    private lateinit var messageProperties: MessageProperties
    private lateinit var message: Message

    @BeforeEach
    fun setUp() {
        assignedIpUuid = Random.nextLong()
        issuerUuid = Random.nextLong()
        assignerUuid = Random.nextLong()
        assignedAt = LocalDate.now().withDayOfYear(1)
        expireAt = LocalDate.now().withDayOfYear((2..365).random())
        ip = (1..4).map { (0..255).random() }
        ipAssignedEventHandler = mock()
        loggingService = mock()
        messageSubscribeService = mock()
        messagePublishService = mock()

        target = IpAssignedMessageSubscriber(ipAssignedEventHandler, loggingService, messagePublishService, messageSubscribeService)
        ipDto = IpDto(ip[0], ip[1], ip[2], ip[3])
        assignedIpDto = AssignedIpDto(assignedIpUuid, issuerUuid, assignerUuid, assignedAt, expireAt, ipDto)
        event = IpAssignedEvent(
            issuerUuid, assignerUuid, assignedAt, expireAt,
            ip[0], ip[1], ip[2], ip[3]
        )
        message = mock()
        messageProperties = mock()
    }

    //이벤트가 IP_ASSIGNED 일 경우
    //페이로드를 해석하여,
    //IP를 할당시킨다. -> Service 단에 위임
    //만약 할당중, 오류발생시, IP ASSIGN ERROR 를 전파한다.
    @Test @DisplayName("이벤트 구독 - 이벤트 구독 및 위임 성공")
    fun subscribeSuccess() {
        val eventStr = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .writeValueAsString(event)
        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.IP_ASSIGNED.routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn(eventStr.toByteArray())
        whenever(ipAssignedEventHandler.handle(eq(assignedIpDto.copy(uuid = 0)), any())).thenReturn(assignedIpDto.toMono())
        whenever(loggingService.withLog(any<Mono<IpAssignedEvent>>(), any<Mono<AssignedIpDto>>(), any())).thenAnswer { return@thenAnswer it.arguments[1] }

        target.subscribe(message)
        verify(ipAssignedEventHandler, times(1)).handle(eq(assignedIpDto.copy(uuid = 0)), any())
        verify(messagePublishService, never()).publishError(any<Error>(), any())
    }

    @Test @DisplayName("이벤트 구독 - 잘못된 이벤트를 구독하였을 경우")
    fun subscribeMalformedPayload() {
        val eventStr = "test${Random.nextLong()}"
        whenever(messageProperties.receivedRoutingKey).thenReturn(Event.IP_ASSIGNED.routingKey)
        whenever(message.messageProperties).thenReturn(messageProperties)
        whenever(message.body).thenReturn(eventStr.toByteArray())
        whenever(loggingService.withLog(any<Mono<IpAssignedEvent>>(), any<Mono<AssignedIpDto>>(), any())).thenAnswer { return@thenAnswer it.arguments[1] }

        target.subscribe(message)
        verify(ipAssignedEventHandler, never()).handle(eq(assignedIpDto), any())
        verify(messagePublishService, times(1)).publishError(Error.WRONG_PAYLOAD, WrongPayloadError(Event.IP_ASSIGNED))
    }
}