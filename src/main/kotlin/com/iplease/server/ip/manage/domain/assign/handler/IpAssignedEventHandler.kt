package com.iplease.server.ip.manage.domain.assign.handler

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto

interface IpAssignedEventHandler {
    fun handle(dto: AssignedIpDto)
}
