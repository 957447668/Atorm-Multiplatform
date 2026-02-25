package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.batch.BatchId
import com.zxhhyj.atorm.openai.api.batch.BatchRequest
import com.zxhhyj.atorm.openai.api.core.PaginatedList
import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.api.exception.OpenAIAPIException
import com.zxhhyj.atorm.openai.client.Batch
import com.zxhhyj.atorm.openai.client.internal.extension.beta
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.path

/**
 * Implementation of [Batch].
 */
internal class BatchApi(val requester: HttpRequester) : Batch {

    override suspend fun batch(
        request: BatchRequest,
        requestOptions: RequestOptions?
    ): Batch {
        return requester.perform {
            it.post {
                url(path = ApiPath.Batches)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun batch(id: BatchId, requestOptions: RequestOptions?): Batch? {
        try {
            return requester.perform<HttpResponse> {
                it.get {
                    url(path = "${ApiPath.Batches}/${id.id}")
                    requestOptions(requestOptions)
                }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun cancel(id: BatchId, requestOptions: RequestOptions?): Batch? {
        val response = requester.perform<HttpResponse> {
            it.post {
                url(path = "${ApiPath.Batches}/${id.id}/cancel")
                requestOptions(requestOptions)
            }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun batches(
        after: BatchId?,
        limit: Int?,
        requestOptions: RequestOptions?
    ): PaginatedList<Batch> {
        return requester.perform {
            it.get {
                url {
                    path(ApiPath.Batches)
                    limit?.let { parameter("limit", it) }
                    after?.let { parameter("after", it.id) }
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }
}
