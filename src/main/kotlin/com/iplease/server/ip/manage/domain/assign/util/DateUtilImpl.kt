package com.iplease.server.ip.manage.domain.assign.util

import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DateUtilImpl: DateUtil {
    override fun dateNow() = LocalDate.now()!!
}