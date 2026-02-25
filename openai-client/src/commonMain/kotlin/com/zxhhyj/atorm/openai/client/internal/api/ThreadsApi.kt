package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.core.DeleteResponse
import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.api.exception.OpenAIAPIException
import com.zxhhyj.atorm.openai.api.thread.Thread
import com.zxhhyj.atorm.openai.api.thread.ThreadId
import com.zxhhyj.atorm.openai.api.thread.ThreadRequest
import com.zxhhyj.atorm.openai.client.Threads
import com.zxhhyj.atorm.openai.client.internal.extension.beta
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

internal class ThreadsApi(val requester: HttpRequester) : Threads {

    override suspend fun thread(request: ThreadRequest?, requestOptions: RequestOptions?): Thread {
        return requester.perform {
            it.post {
                url(path = ApiPath.Threads)
                request?.let { req ->
                    setBody(req)
                    contentType(ContentType.Application.Json)
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun thread(id: ThreadId, requestOptions: RequestOptions?): Thread? {
        try {
            return requester.perform<HttpResponse> {
                it.get {
                    url(path = "${ApiPath.Threads}/${id.id}")
                    beta("assistants", 2)
                    requestOptions(requestOptions)
                }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun thread(id: ThreadId, metadata: Map<String, String>, requestOptions: RequestOptions?): Thread {
        val request = buildJsonObject {
            putJsonObject("metadata") {
                metadata.forEach { (key, value) ->
                    put(key, value)
                }
            }
        }
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun delete(id: ThreadId, requestOptions: RequestOptions?): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.Threads}/${id.id}")
                beta("assistants", 2)
                requestOptions(requestOptions)
            }
        }
        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }
}
