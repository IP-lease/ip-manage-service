package com.iplease.server.ip.manage.domain.assign.exception

class AlreadyExistsAssignedIpException(val ip: String) : RuntimeException("이미 할당된 IP주소입니다! - $ip")
