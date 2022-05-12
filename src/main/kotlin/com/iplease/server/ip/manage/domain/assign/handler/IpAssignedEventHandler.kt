package com.iplease.server.ip.manage.domain.assign.handler

import com.iplease.server.ip.manage.global.common.data.dto.AssignedIpDto
import com.iplease.server.ip.manage.global.common.handler.EventHandler

interface IpAssignedEventHandler: EventHandler<AssignedIpDto, AssignedIpDto>
