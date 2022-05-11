package com.iplease.server.ip.manage.global.release

import java.time.LocalDate

interface IpReleaseReserveService {
    fun reserve(uuid: Long, issuerUuid: Long, expireAt: LocalDate)
}
