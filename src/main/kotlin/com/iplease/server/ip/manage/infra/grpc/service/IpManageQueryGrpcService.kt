package com.iplease.server.ip.manage.infra.grpc.service

import com.google.protobuf.BoolValue
import com.google.protobuf.Int64Value
import com.iplease.lib.ip.manage.AssignedIp
import com.iplease.lib.ip.manage.Date
import com.iplease.lib.ip.manage.Ip
import com.iplease.lib.ip.manage.ReactorIpManageQueryServiceGrpc
import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.common.data.dto.IpDto
import com.iplease.server.ip.manage.global.common.service.IpManageQueryService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class IpManageQueryGrpcService(
    val ipManageQueryService: IpManageQueryService
): ReactorIpManageQueryServiceGrpc.IpManageQueryServiceImplBase() {
    override fun existsAssignedIpByUuid(request: Mono<Int64Value>): Mono<BoolValue> =
        request.flatMap { ipManageQueryService.existsAssignedIpByUuid(it.value) }
            .map { it.toGrpcMessage() }

    override fun getAssignedIpByUuid(request: Mono<Int64Value>): Mono<AssignedIp> =
        request.flatMap { ipManageQueryService.getAssignedIpByUuid(it.value) }
            .map { it.toGrpcMessage() }

    private fun AssignedIpDto.toGrpcMessage() =
        AssignedIp.newBuilder()
            .also {
                it.uuid = uuid
                it.issuerUuid = issuerUuid
                it.assignerUuid = assignerUuid
                it.assignedAt = assignedAt.toGrpcMessage()
                it.expireAt = expireAt.toGrpcMessage()
                it.ip = ip.toGrpcMessage()
            }.build()

    private fun IpDto.toGrpcMessage(): Ip =
        Ip.newBuilder().also {
            it.first = first
            it.second = second
            it.third = third
            it.fourth = fourth
        }.build()

    private fun LocalDate.toGrpcMessage() =
        Date.newBuilder().also {
            it.year = year
            it.month = monthValue
            it.day = dayOfMonth
        }.build()

    private fun Boolean.toGrpcMessage() =
        BoolValue.newBuilder()
            .also { it.value = this }
            .build()
}