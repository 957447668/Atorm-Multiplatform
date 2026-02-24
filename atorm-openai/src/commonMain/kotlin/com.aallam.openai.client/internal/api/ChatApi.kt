package com.aallam.openai.client.internal.api

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.Chat
import com.aallam.openai.client.internal.extension.buildChatCompletionRequest
import com.aallam.openai.client.internal.extension.buildStreamChatCompletionRequest
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.extension.streamEventsFrom
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.json.JsonObject

internal class ChatApi(private val requester: HttpRequester) : Chat {
    override suspend fun chatCompletion(
        request: ChatCompletionRequest,
        options: JsonObject?,
        requestOptions: RequestOptions?
    ): ChatCompletion {
        return requester.perform {
            it.post {
                url(path = ApiPath.ChatCompletions)
                setBody(buildChatCompletionRequest(request, options))
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override fun chatCompletions(
        request: ChatCompletionRequest,
        options: JsonObject?,
        requestOptions: RequestOptions?
    ): Flow<ChatCompletionChunk> {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = ApiPath.ChatCompletions)
            setBody(buildStreamChatCompletionRequest(request, options))
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.Connection, "keep-alive")
            }
            requestOptions(requestOptions)
        }
        return channelFlow {
            requester.perform(builder) { response -> streamEventsFrom(response) }
        }
    }
}
