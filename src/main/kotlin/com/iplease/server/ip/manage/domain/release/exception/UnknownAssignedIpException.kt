package com.iplease.server.ip.manage.domain.release.exception

class UnknownAssignedIpException(val uuid: Long) : RuntimeException("존재하지 않는 할당IP 입니다! - $uuid")
