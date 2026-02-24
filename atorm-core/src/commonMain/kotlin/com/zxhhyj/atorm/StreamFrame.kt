package com.zxhhyj.atorm

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@Serializable
public sealed interface StreamFrame {
    @Serializable
    public data class Append(
        val text: String
    ) : StreamFrame

    @Serializable
    public data class ToolCall(
        val id: String?,
        val name: String,
        val content: String
    ) : StreamFrame {

        val contentJsonResult: Result<JsonObject> by lazy {
            runCatching { Json.parseToJsonElement(content).jsonObject }
        }

        val contentJson: JsonObject
            get() = contentJsonResult.getOrThrow()
    }

    @Serializable
    public data class End(
        val finishReason: String? = null,
        val metaInfo: ResponseMetaInfo = ResponseMetaInfo.Empty
    ) : StreamFrame
}
