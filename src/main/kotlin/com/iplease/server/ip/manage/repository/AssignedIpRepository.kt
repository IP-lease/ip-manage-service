package com.iplease.server.ip.manage.repository

import com.iplease.server.ip.manage.table.AssignedIpTable
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface AssignedIpRepository: R2dbcRepository<AssignedIpTable, Long>
