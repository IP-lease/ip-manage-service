package com.iplease.server.ip.manage.assign.service

import com.iplease.server.ip.manage.assign.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.assign.data.dto.IpDto
import com.iplease.server.ip.manage.assign.repository.AssignedIpRepository
import com.iplease.server.ip.manage.assign.data.table.AssignedIpTable
import com.iplease.server.ip.manage.assign.exception.WrongExpireDateException
import com.iplease.server.ip.manage.assign.util.DateUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.random.Random

class IpAssignServiceImplTest {
    private lateinit var repository: AssignedIpRepository
    private lateinit var dateUtil: DateUtil
    private lateinit var ipAssignService: IpAssignService

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
        ipAssignService = IpAssignServiceImpl(dateUtil, repository)

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
        whenever(dateUtil.dateNow()).thenReturn(expireAt.minusYears(2).withDayOfYear(1))
        whenever(repository.save(table.copy(uuid = 0))).thenReturn(table.toMono())

        val result = ipAssignService.assign(dto).block()

        assert(result == dto)
        verify(repository, times(1)).save(table.copy(uuid = 0))
    }

    //expireAt 이 assignedAt 이전일 경우
    @Test @DisplayName("IP 할당 - 할당 만료일이 할당일 이전일 경우")
    fun assignFailureExpireIsBeforeThenAssignedAt() {
        val tmp = expireAt
        expireAt = assignedAt
        assignedAt = tmp

        val dto = AssignedIpDto(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, IpDto(ipFirst, ipSecond, ipThird, ipFourth))
        val table = AssignedIpTable(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, ipFirst, ipSecond, ipThird, ipFourth)
        whenever(dateUtil.dateNow()).thenReturn(expireAt.minusYears(2).withDayOfYear(1))
        whenever(repository.save(table.copy(uuid = 0))).thenReturn(table.toMono())

        val exception = assertThrows<WrongExpireDateException> { ipAssignService.assign(dto).block() }

        assert(exception.date == expireAt)
        verify(repository, times(0)).save(table.copy(uuid = 0))
    }

    //expireAt 이 오늘 또는 과거일경우
    @Test @DisplayName("IP 할당 - 할당 만료일이 할당일 이전일 경우")
    fun assignFailureExpireIsBeforeThenNow() {
        val dto = AssignedIpDto(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, IpDto(ipFirst, ipSecond, ipThird, ipFourth))
        val table = AssignedIpTable(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, ipFirst, ipSecond, ipThird, ipFourth)
        whenever(dateUtil.dateNow()).thenReturn(expireAt.plusYears(1).withDayOfYear(1))
        whenever(repository.save(table.copy(uuid = 0))).thenReturn(table.toMono())

        val exception = assertThrows<WrongExpireDateException> { ipAssignService.assign(dto).block() }

        assert(exception.date == expireAt)
        verify(repository, times(0)).save(table.copy(uuid = 0))
    }
}