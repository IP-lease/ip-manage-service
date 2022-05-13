package com.iplease.server.ip.manage.domain.common.service

import com.iplease.server.ip.manage.global.common.data.table.AssignedIpTable
import com.iplease.server.ip.manage.global.common.repository.AssignedIpRepository
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

class IpManageQueryServiceImplTest {
    private lateinit var assignedIpRepository: AssignedIpRepository
    private lateinit var target: IpManageQueryServiceImpl
    private var uuid by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp() {
        assignedIpRepository = mock()
        target = IpManageQueryServiceImpl(assignedIpRepository)
        uuid = Random.nextLong()
    }

    @Test
    fun existsAssignedIpByUuidSuccess() {
        val isExist = listOf(true, false).random()
        whenever(assignedIpRepository.existsById(uuid)).thenReturn(isExist.toMono())
        val result = target.existsAssignedIpByUuid(uuid).block()
        verify(assignedIpRepository, times(1)).existsById(uuid)
        assert(result == isExist)
    }

    @Test
    fun getAssignedIpByUuidSuccess() {
        val table = AssignedIpTable(
            uuid, Random.nextLong(), Random.nextLong(),
            LocalDate.now(), LocalDate.now().plusDays(1),
            Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt()
        )
        whenever(assignedIpRepository.findById(uuid)).thenReturn(table.toMono())

        val result = target.getAssignedIpByUuid(uuid).block()!!

        verify(assignedIpRepository, times(1)).findById(uuid)
        assert(result.uuid == table.uuid)
        assert(result.issuerUuid == table.issuerUuid)
        assert(result.assignerUuid == table.assignerUuid)
        assert(result.assignedAt == table.assignedAt)
        assert(result.expireAt == table.expireAt)
        assert(result.ip.first == table.ipFirst)
        assert(result.ip.second == table.ipSecond)
        assert(result.ip.third == table.ipThird)
        assert(result.ip.fourth == table.ipFourth)
    }
}