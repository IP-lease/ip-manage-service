package com.iplease.server.ip.manage.infra.message.service.publish.event

import com.iplease.server.ip.manage.infra.message.data.type.Event
import com.iplease.server.ip.manage.infra.message.service.publish.MessagePublishService

interface EventMessagePublishService: MessagePublishService<Event>