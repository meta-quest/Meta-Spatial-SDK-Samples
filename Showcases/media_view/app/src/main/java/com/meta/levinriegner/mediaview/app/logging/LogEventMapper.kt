// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.logging

import com.datadog.android.event.EventMapper
import com.datadog.android.log.model.LogEvent
import java.util.UUID

class LogEventMapper : EventMapper<LogEvent> {

    private val sessionId = UUID.randomUUID().toString()

    override fun map(event: LogEvent): LogEvent {
        event.additionalProperties["session.id"] = sessionId
        return event
    }
}