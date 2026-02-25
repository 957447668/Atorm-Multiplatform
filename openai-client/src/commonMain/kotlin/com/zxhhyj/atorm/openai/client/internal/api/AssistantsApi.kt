package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.assistant.Assistant
import com.zxhhyj.atorm.openai.api.assistant.AssistantId
import com.zxhhyj.atorm.openai.api.assistant.AssistantRequest
import com.zxhhyj.atorm.openai.api.core.*
import com.zxhhyj.atorm.openai.api.exception.OpenAIAPIException
import com.zxhhyj.atorm.openai.client.Assistants
import com.zxhhyj.atorm.openai.client.internal.extension.beta
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class AssistantsApi(val requester: HttpRequester) : Assistants {
    override suspend fun assistant(request: AssistantRequest, requestOptions: RequestOptions?): Assistant {
        return requester.perform {
            it.post {
                url(path = ApiPath.Assistants)
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun assistant(id: AssistantId, requestOptions: RequestOptions?): Assistant? {
        try {
            return requester.perform<HttpResponse> {
                it.get {
                    url(path = "${ApiPath.Assistants}/${id.id}")
                    beta("assistants", 2)
                    requestOptions(requestOptions)
                }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun assistant(
        id: AssistantId,
        request: AssistantRequest,
        requestOptions: RequestOptions?
    ): Assistant {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Assistants}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun delete(id: AssistantId, requestOptions: RequestOptions?): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.Assistants}/${id.id}")
                beta("assistants", 2)
                requestOptions(requestOptions)
            }
        }
        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }

    override suspend fun assistants(
        limit: Int?,
        order: SortOrder?,
        after: AssistantId?,
        before: AssistantId?,
        requestOptions: RequestOptions?
    ): List<Assistant> {
        return requester.perform<ListResponse<Assistant>> { client ->
            client.get {
                url {
                    path(ApiPath.Assistants)
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it.order) }
                    after?.let { parameter("after", it.id) }
                    before?.let { parameter("before", it.id) }
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }
}
