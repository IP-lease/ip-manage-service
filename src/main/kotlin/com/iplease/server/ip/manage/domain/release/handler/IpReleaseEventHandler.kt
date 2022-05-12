package com.iplease.server.ip.manage.domain.release.handler

import com.iplease.server.ip.manage.global.common.data.dto.ReleasedIpDto
import com.iplease.server.ip.manage.global.common.handler.EventHandler

interface IpReleaseEventHandler: EventHandler<ReleasedIpDto, Unit>