package com.iplease.server.ip.manage.infra.message.service.publish.error

import com.iplease.server.ip.manage.infra.message.data.type.Error
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishService

interface ErrorMessagePublishService: MessagePublishService<Error>
