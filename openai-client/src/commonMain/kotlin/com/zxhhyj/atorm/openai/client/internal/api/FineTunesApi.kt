package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.core.DeleteResponse
import com.zxhhyj.atorm.openai.api.core.ListResponse
import com.zxhhyj.atorm.openai.api.finetune.FineTune
import com.zxhhyj.atorm.openai.api.finetune.FineTuneEvent
import com.zxhhyj.atorm.openai.api.finetune.FineTuneId
import com.zxhhyj.atorm.openai.api.finetune.FineTuneRequest
import com.zxhhyj.atorm.openai.api.model.ModelId
import com.zxhhyj.atorm.openai.client.FineTunes
import com.zxhhyj.atorm.openai.client.internal.extension.streamEventsFrom
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * Implementation of [FineTunes].
 */
internal class FineTunesApi(private val requester: HttpRequester) : FineTunes {

    override suspend fun fineTune(request: FineTuneRequest): FineTune {
        return requester.perform {
            it.post {
                url(path = ApiPath.FineTunes)
                setBody(request)
                contentType(ContentType.Application.Json)
            }
        }
    }

    override suspend fun fineTune(fineTuneId: FineTuneId): FineTune? {
        val response = requester.perform<HttpResponse> {
            it.get { url(path = "${ApiPath.FineTunes}/${fineTuneId.id}") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun fineTunes(): List<FineTune> {
        return requester.perform<ListResponse<FineTune>> {
            it.get { url(path = ApiPath.FineTunes) }
        }.data
    }

    override suspend fun cancel(fineTuneId: FineTuneId): FineTune? {
        val response = requester.perform<HttpResponse> {
            it.post { url(path = "${ApiPath.FineTunes}/${fineTuneId.id}/cancel") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun fineTuneEvents(fineTuneId: FineTuneId): List<FineTuneEvent> {
        return requester.perform<ListResponse<FineTuneEvent>> {
            it.get { url(path = "${ApiPath.FineTunes}/${fineTuneId.id}/events") }
        }.data
    }

    override fun fineTuneEventsFlow(fineTuneId: FineTuneId): Flow<FineTuneEvent> {
        val request = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(path = "${ApiPath.FineTunes}/${fineTuneId.id}/events") {
                parameters.append("stream", "true")
            }
            accept(ContentType.Text.EventStream)
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.Connection, "keep-alive")
            }
        }
        return channelFlow {
            requester.perform(request) { response -> streamEventsFrom(response) }
        }
    }

    override suspend fun delete(fineTuneModel: ModelId): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.Models}/${fineTuneModel.id}")
            }
        }
        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }
}
