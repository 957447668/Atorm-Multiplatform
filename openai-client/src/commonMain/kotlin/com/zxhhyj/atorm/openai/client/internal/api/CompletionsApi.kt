package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.completion.CompletionRequest
import com.zxhhyj.atorm.openai.api.completion.TextCompletion
import com.zxhhyj.atorm.openai.client.Completions
import com.zxhhyj.atorm.openai.client.internal.extension.buildCompletionRequest
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

/**
 * Implementation of [Completions].
 */
internal class CompletionsApi(private val requester: HttpRequester) : Completions {

    override suspend fun completion(request: CompletionRequest): TextCompletion {
        return requester.perform {
            it.post {
                url(path = ApiPath.Completions)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    override fun completions(request: CompletionRequest): Flow<TextCompletion> {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = ApiPath.Completions)
            setBody(buildCompletionRequest(request))
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.Connection, "keep-alive")
            }
        }
        return channelFlow {
            requester.perform(builder) { response -> streamEventsFrom(response) }
        }
    }
}
