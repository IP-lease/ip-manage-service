package com.iplease.server.ip.manage.assign.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.manage.global.assign.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.assign.data.dto.IpDto
import com.iplease.server.ip.manage.domain.assign.service.IpAssignService
import com.iplease.server.ip.manage.domain.assign.listener.IpAssignedEventListener
import com.iplease.server.ip.manage.infra.event.data.dto.IpAssignedError
import com.iplease.server.ip.manage.infra.event.data.dto.IpAssignedEvent
import com.iplease.server.ip.manage.infra.event.service.EventPublishService
import com.iplease.server.ip.manage.infra.event.service.EventSubscribeService
import com.iplease.server.ip.manage.infra.event.data.type.Error
import com.iplease.server.ip.manage.infra.event.data.type.Event
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.random.Random

class IpAssignedEventListenerTest {
    private lateinit var ipAssignService: IpAssignService
    private lateinit var eventPublishService: EventPublishService
    private lateinit var eventSubscribeService: EventSubscribeService
    private lateinit var target: IpAssignedEventListener
    private var assignedIpUuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()
    private var assignerUuid by Delegates.notNull<Long>()
    private lateinit var assignedAt: LocalDate
    private lateinit var expireAt: LocalDate
    private lateinit var ip: List<Int>
    private lateinit var ipDto: IpDto
    private lateinit var assignedIpDto: AssignedIpDto
    private lateinit var event: IpAssignedEvent

    @BeforeEach
    fun setUp() {
        assignedIpUuid = Random.nextLong()
        issuerUuid = Random.nextLong()
        assignerUuid = Random.nextLong()
        assignedAt = LocalDate.now().withDayOfYear(1)
        expireAt = LocalDate.now().withDayOfYear((2..365).random())
        ip = (1..4).map { (0..255).random() }
        ipAssignService = mock()
        eventPublishService = mock()
        eventSubscribeService = mock()

        target = IpAssignedEventListener(ipAssignService, eventPublishService, eventSubscribeService)
        ipDto = IpDto(ip[0], ip[1], ip[2], ip[3])
        assignedIpDto = AssignedIpDto(assignedIpUuid, issuerUuid, assignerUuid, assignedAt, expireAt, ipDto)
        event = IpAssignedEvent(
            issuerUuid, assignerUuid, assignedAt, expireAt,
            ip[0], ip[1], ip[2], ip[3]
        )
    }

    //이벤트가 IP_ASSIGNED 일 경우
    //페이로드를 해석하여,
    //IP를 할당시킨다. -> Service 단에 위임
    //만약 할당중, 오류발생시, IP ASSIGN ERROR 를 전파한다.
    @Test
    fun handleSuccess() {
        val eventStr = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .writeValueAsString(event)
        val prop = mock<MessageProperties> {
            on{ receivedRoutingKey }.thenReturn(Event.IP_ASSIGNED.routingKey)
        }
        val message = mock<Message>() {
            on { body }.thenReturn(eventStr.toByteArray())
            on { messageProperties }.thenReturn(prop)
        }
        whenever(ipAssignService.assign(assignedIpDto.copy(uuid = 0))).thenReturn(assignedIpDto.toMono())

        target.handle(message)

        verify(ipAssignService, times(1)).assign(assignedIpDto.copy(uuid = 0))
    }

    @Test
    fun handleFailure() {
        val throwable = java.lang.RuntimeException()
        val error = IpAssignedError(
            issuerUuid, assignerUuid, assignedAt, expireAt,
            ip[0], ip[1], ip[2], ip[3],
            throwable
        )
        val eventStr = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .writeValueAsString(event)
        val prop = mock<MessageProperties> {
            on{ receivedRoutingKey }.thenReturn(Event.IP_ASSIGNED.routingKey)
        }
        val message = mock<Message>() {
            on { body }.thenReturn(eventStr.toByteArray())
            on { messageProperties }.thenReturn(prop)
        }
        whenever(ipAssignService.assign(assignedIpDto.copy(uuid = 0)))
            .thenReturn(Mono.error(throwable))

        target.handle(message)

        verify(ipAssignService, times(1)).assign(assignedIpDto.copy(uuid = 0))
        verify(eventPublishService, times(1)).publish(Error.IP_ASSIGNED.routingKey, error)
    }
}