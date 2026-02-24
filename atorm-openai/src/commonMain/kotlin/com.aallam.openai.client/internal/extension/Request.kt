package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.io.buffered
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

internal fun buildCompletionRequest(request: CompletionRequest): JsonObject {
    val json = JsonLenient.encodeToJsonElement(request)
    val map = json.jsonObject.toMutableMap().also { it += "stream" to JsonPrimitive(true) }
    return JsonObject(map)
}

internal fun buildChatCompletionRequest(
    request: ChatCompletionRequest,
    options: JsonObject?
): JsonObject {
    val json = JsonLenient.encodeToJsonElement(request)
    val map = options?.let { json.jsonObject.toMutableMap().plus(options.toMap()) } ?: json.jsonObject.toMap()
    return JsonObject(map)
}

internal fun buildStreamChatCompletionRequest(
    request: ChatCompletionRequest,
    options: JsonObject?
): JsonObject {
    return buildChatCompletionRequest(request, buildJsonObject {
        put("stream", true)
        options?.toMap()?.forEach {
            put(it.key, it.value)
        }
    })
}

internal fun FormBuilder.appendFileSource(
    key: String,
    fileSource: FileSource,
    contentType: ContentType = ContentType.Application.OctetStream
) {
    append(key = key, filename = fileSource.name, contentType = contentType) {
        fileSource.source.buffered().use { source ->
            val buffer = ByteArray(8192) // 8 KiB
            var bytesRead: Int
            while (source.readAvailable(buffer).also { bytesRead = it } != 0) {
                writeFully(buffer = buffer, offset = 0, length = bytesRead)
            }
        }
    }
}

internal fun HttpMessageBuilder.beta(api: String, version: Int) {
    header("OpenAI-Beta", "$api=v$version")
}
