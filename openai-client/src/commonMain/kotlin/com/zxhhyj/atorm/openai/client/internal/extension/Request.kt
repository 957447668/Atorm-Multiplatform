package com.zxhhyj.atorm.openai.client.internal.extension

import com.zxhhyj.atorm.openai.api.chat.ChatCompletionRequest
import com.zxhhyj.atorm.openai.api.completion.CompletionRequest
import com.zxhhyj.atorm.openai.api.file.FileSource
import com.zxhhyj.atorm.openai.client.internal.JsonLenient
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.append
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.utils.io.core.readAvailable
import io.ktor.utils.io.core.writeFully
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
