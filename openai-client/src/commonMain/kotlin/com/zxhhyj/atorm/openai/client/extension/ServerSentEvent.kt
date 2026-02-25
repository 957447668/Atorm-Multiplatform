package com.zxhhyj.atorm.openai.client.extension

import com.zxhhyj.atorm.openai.api.run.AssistantStreamEvent
import com.zxhhyj.atorm.openai.api.run.AssistantStreamEventType
import io.ktor.sse.ServerSentEvent

/**
 * Convert a [io.ktor.sse.ServerSentEvent] to [com.zxhhyj.atorm.openai.api.run.AssistantStreamEvent]. Type will be [com.zxhhyj.atorm.openai.api.run.AssistantStreamEventType.UNKNOWN] if the event is null or unrecognized.
 */
internal fun ServerSentEvent.toAssistantStreamEvent(): AssistantStreamEvent =
    AssistantStreamEvent(
        event,
        event
            ?.let(AssistantStreamEventType::fromEvent)
            ?: AssistantStreamEventType.UNKNOWN,
        data
    )
