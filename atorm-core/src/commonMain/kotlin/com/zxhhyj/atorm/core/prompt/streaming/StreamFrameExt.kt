package com.zxhhyj.atorm.core.prompt.streaming

import com.zxhhyj.atorm.core.prompt.message.Message
import com.zxhhyj.atorm.core.prompt.message.ResponseMetaInfo

public fun Message.Response.toStreamFrame(): StreamFrame =
    when (this) {
        is Message.Assistant -> StreamFrame.Append(content)
        is Message.Reasoning -> StreamFrame.Append(content)
        is Message.Tool.Call -> StreamFrame.ToolCall(id, tool, content)
    }

public fun Iterable<StreamFrame>.toMessageResponses(): List<Message.Response> {
    var assistantContent: String? = null
    val toolCalls = mutableListOf<StreamFrame.ToolCall>()
    var end: StreamFrame.End? = null

    forEach { frame ->
        when (frame) {
            is StreamFrame.Append -> assistantContent = (assistantContent ?: "") + frame.text
            is StreamFrame.ToolCall -> toolCalls += frame
            is StreamFrame.End -> end = frame
        }
    }

    return buildList {
        toolCalls.forEach {
            add(
                Message.Tool.Call(
                    id = it.id,
                    tool = it.name,
                    content = it.content,
                    metaInfo = end?.metaInfo ?: ResponseMetaInfo.Empty
                )
            )
        }
        assistantContent?.let {
            add(
                Message.Assistant(
                    content = it,
                    finishReason = end?.finishReason,
                    metaInfo = end?.metaInfo ?: ResponseMetaInfo.Empty
                )
            )
        }
    }
}

public fun Iterable<StreamFrame>.toToolCallMessages(): List<Message.Tool.Call> =
    toMessageResponses().filterIsInstance<Message.Tool.Call>()

public fun Iterable<StreamFrame>.toAssistantMessageOrNull(): Message.Assistant? =
    toMessageResponses().filterIsInstance<Message.Assistant>().singleOrNull()
