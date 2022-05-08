package com.iplease.server.ip.manage.assign.repository

import com.iplease.server.ip.manage.assign.table.AssignedIpTable
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface AssignedIpRepository: R2dbcRepository<AssignedIpTable, Long>
