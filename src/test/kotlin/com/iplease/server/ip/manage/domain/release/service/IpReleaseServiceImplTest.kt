package com.iplease.server.ip.manage.domain.release.service

import com.iplease.server.ip.manage.domain.release.exception.UnknownAssignedIpException
import com.iplease.server.ip.manage.global.common.repository.AssignedIpRepository
import com.iplease.server.ip.manage.infra.log.service.LoggingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.random.Random

class IpReleaseServiceImplTest {
    private lateinit var assignedIpRepository: AssignedIpRepository
    private lateinit var loggingService: LoggingService
    private lateinit var target: IpReleaseServiceImpl

    @BeforeEach
    fun setUp() {
        assignedIpRepository = mock()
        loggingService = mock()
        target = IpReleaseServiceImpl(assignedIpRepository, loggingService)
    }

    @Test @DisplayName("할당IP 해제 - 해제 성공")
    fun releaseSuccess() {
        val assignedIpUuid = Random.nextLong()
        whenever(assignedIpRepository.deleteById(assignedIpUuid)).thenReturn(true.toMono().then())
        whenever(assignedIpRepository.existsById(assignedIpUuid)).thenReturn(true.toMono())
        whenever(loggingService.withLog(any<Long>(), any<Mono<Void>>(), any())).thenAnswer { return@thenAnswer it.arguments[1] }

        target.release(assignedIpUuid).block()

        verify(assignedIpRepository, times(1)).deleteById(assignedIpUuid)
    }

    @Test @DisplayName("할당IP 해제 - 할당IP가 존재하지 않을 경우")
    fun releaseFailureNotExist() {
        val assignedIpUuid = Random.nextLong()
        whenever(assignedIpRepository.existsById(assignedIpUuid)).thenReturn(false.toMono())
        whenever(loggingService.withLog(any<Long>(), any<Mono<Void>>(), any())).thenAnswer { return@thenAnswer it.arguments[1] }

        val exception = assertThrows<UnknownAssignedIpException> { target.release(assignedIpUuid).block() }

        assert(exception.uuid == assignedIpUuid)
        verify(assignedIpRepository, times(0)).deleteById(assignedIpUuid)
    }
}