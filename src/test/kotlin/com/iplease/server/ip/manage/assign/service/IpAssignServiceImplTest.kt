package com.iplease.server.ip.manage.assign.service

import com.iplease.server.ip.manage.assign.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.assign.data.dto.IpDto
import com.iplease.server.ip.manage.assign.repository.AssignedIpRepository
import com.iplease.server.ip.manage.assign.data.table.AssignedIpTable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        ipAssignService = IpAssignServiceImpl(repository)

        uuid = Random.nextLong()
        issuerUuid = Random.nextLong()
        assignerUuid = Random.nextLong()
        assignedAt = LocalDate.now()
        expireAt = LocalDate.now()
        ipFirst = Random.nextInt()
        ipSecond = Random.nextInt()
        ipThird = Random.nextInt()
        ipFourth = Random.nextInt()
    }

    @Test
    fun assignSuccess() {
        val ipDto = IpDto(ipFirst, ipSecond, ipThird, ipFourth)
        val dto = AssignedIpDto(uuid, issuerUuid, assignerUuid, assignedAt, expireAt, ipDto)
        val table = AssignedIpTable(
            uuid, issuerUuid, assignerUuid, assignedAt, expireAt,
            ipFirst, ipSecond, ipThird, ipFourth
        )
        whenever(repository.save(table.copy(uuid = 0))).thenReturn(table.toMono())

        val result = ipAssignService.assign(dto).block()

        assert(result == dto)
        verify(repository, times(1)).save(table.copy(uuid = 0))
    }
}