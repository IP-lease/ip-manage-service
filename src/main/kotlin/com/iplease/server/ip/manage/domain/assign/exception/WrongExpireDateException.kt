package com.iplease.server.ip.manage.domain.assign.exception

import java.time.LocalDate

class WrongExpireDateException(val date: LocalDate, msg: String) : RuntimeException("잘못된 만료일 입니다! $msg - $date")
