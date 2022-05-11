package com.iplease.server.ip.manage.domain.assign.service

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.common.data.dto.IpDto
import com.iplease.server.ip.manage.global.common.repository.AssignedIpRepository
import com.iplease.server.ip.manage.global.common.data.table.AssignedIpTable
import com.iplease.server.ip.manage.domain.assign.exception.AlreadyExistsAssignedIpException
import com.iplease.server.ip.manage.domain.assign.exception.WrongExpireDateException
import com.iplease.server.ip.manage.domain.assign.util.DateUtil
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import com.iplease.server.ip.manage.infra.log.type.LoggerType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.random.Random

class IpAssignServiceImplTest {
    private lateinit var repository: AssignedIpRepository
    private lateinit var dateUtil: DateUtil
    private lateinit var ipAssignService: IpAssignService
    private lateinit var loggingService: LoggingService

    private var uuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()
    private var assignerUuid by Delegates.notNull<Long>()
    private lateinit var assignedAt: LocalDate
    private lateinit var expireAt: LocalDate
    private var ipFirst by Delegates.notNull<Int>()
    private var ipSecond by Delegates.notNull<Int>()
    private var ipThird by Delegates.notNull<Int>()
    private var ipFourth by Delegates.notNull<Int>()

    @BeforeEach
    fun setUp() {
        repository = mock()
        dateUtil = mock ()
        loggingService = mock() {
            on{ withLog(any<AssignedIpDto>(), any<Mono<AssignedIpDto>>(), eq(LoggerType.IP_ASSIGN_LOGGER)) }.thenAnswer{ it.arguments[1] as Mono<*> }
        }
        ipAssignService = IpAssignServiceImpl(dateUtil, repository, loggingService)

        uuid = Random.nextLong()
        issuerUuid = Random.nextLong()
        assignerUuid = Random.nextLong()
        assignedAt = LocalDate.now().plusYears(1).withDayOfYear(1)
        expireAt = LocalDate.now().plusYears(1).withDayOfYear((2..365).random())
        ipFirst = Random.nextInt()
        ipSecond = Random.nextInt()
        ipThird = Random.nextInt()
        ipFourth = Random.nextInt()
    }

    @Test @DisplayName("IP 할당 - 할당 성공")
    fun assignSuccess() {
        val dto = AssignedIpDto(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, IpDto(ipFirst, ipSecond, ipThird, ipFourth))
        val table = AssignedIpTable(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, ipFirst, ipSecond, ipThird, ipFourth)
        whenever(dateUtil.dateNow()).thenReturn(expireAt.minusYears(1).withDayOfYear(1))
        whenever(repository.save(table.copy(uuid = 0))).thenReturn(table.toMono())
        whenever(repository.existsByIpFirstAndIpSecondAndIpThirdAndIpFourth(ipFirst, ipSecond, ipThird, ipFourth)).thenReturn(false.toMono())

        val result = ipAssignService.assign(dto).block()!!

        assert(result == dto)
        verify(repository, times(1)).save(table.copy(uuid = 0))
    }

    @Test @DisplayName("IP 할당 - 할당 만료일이 할당일 이전일 경우")
    fun assignFailureExpireIsBeforeThenAssignedAt() {
        val tmp = expireAt
        expireAt = assignedAt
        assignedAt = tmp

        val dto = AssignedIpDto(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, IpDto(ipFirst, ipSecond, ipThird, ipFourth))
        val table = AssignedIpTable(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, ipFirst, ipSecond, ipThird, ipFourth)
        whenever(dateUtil.dateNow()).thenReturn(expireAt.minusYears(2).withDayOfYear(1))
        whenever(repository.save(table.copy(uuid = 0))).thenReturn(table.toMono())
        whenever(repository.existsByIpFirstAndIpSecondAndIpThirdAndIpFourth(ipFirst, ipSecond, ipThird, ipFourth)).thenReturn(false.toMono())

        val exception = assertThrows<WrongExpireDateException> { ipAssignService.assign(dto).block() }

        assert(exception.date == expireAt)
        verify(repository, times(0)).save(table.copy(uuid = 0))
    }

    @Test @DisplayName("IP 할당 - 할당 만료일이 할당일 이전일 경우")
    fun assignFailureExpireIsBeforeThenNow() {
        val dto = AssignedIpDto(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, IpDto(ipFirst, ipSecond, ipThird, ipFourth))
        val table = AssignedIpTable(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, ipFirst, ipSecond, ipThird, ipFourth)
        whenever(dateUtil.dateNow()).thenReturn(expireAt.plusYears(1).withDayOfYear(1))
        whenever(repository.save(table.copy(uuid = 0))).thenReturn(table.toMono())
        whenever(repository.existsByIpFirstAndIpSecondAndIpThirdAndIpFourth(ipFirst, ipSecond, ipThird, ipFourth)).thenReturn(false.toMono())

        val exception = assertThrows<WrongExpireDateException> { ipAssignService.assign(dto).block() }

        assert(exception.date == expireAt)
        verify(repository, times(0)).save(table.copy(uuid = 0))
    }

    @Test @DisplayName("IP 할당 - 이미 할당된 ip 주소일 경우")
    fun assignFailureAlreadyExists() {
        val dto = AssignedIpDto(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, IpDto(ipFirst, ipSecond, ipThird, ipFourth))
        val table = AssignedIpTable(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, ipFirst, ipSecond, ipThird, ipFourth)
        whenever(dateUtil.dateNow()).thenReturn(expireAt.minusYears(1).withDayOfYear(1))
        whenever(repository.save(table.copy(uuid = 0))).thenReturn(table.toMono())
        whenever(repository.existsByIpFirstAndIpSecondAndIpThirdAndIpFourth(ipFirst, ipSecond, ipThird, ipFourth)).thenReturn(true.toMono())

        val exception = assertThrows<AlreadyExistsAssignedIpException> { ipAssignService.assign(dto).block() }

        assert(exception.ip == "$ipFirst.$ipSecond.$ipThird.$ipFourth")
        verify(repository, times(0)).save(table.copy(uuid = 0))
    }
}