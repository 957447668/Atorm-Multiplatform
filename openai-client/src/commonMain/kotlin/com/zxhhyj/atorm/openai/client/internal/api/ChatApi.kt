package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.chat.ChatCompletion
import com.zxhhyj.atorm.openai.api.chat.ChatCompletionChunk
import com.zxhhyj.atorm.openai.api.chat.ChatCompletionRequest
import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.client.Chat
import com.zxhhyj.atorm.openai.client.internal.extension.buildChatCompletionRequest
import com.zxhhyj.atorm.openai.client.internal.extension.buildStreamChatCompletionRequest
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.extension.streamEventsFrom
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
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
