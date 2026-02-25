package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.api.embedding.EmbeddingRequest
import com.zxhhyj.atorm.openai.api.embedding.EmbeddingResponse
import com.zxhhyj.atorm.openai.client.Embeddings
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Embeddings].
 */
internal class EmbeddingsApi(private val requester: HttpRequester) : Embeddings {

    override suspend fun embeddings(request: EmbeddingRequest, requestOptions: RequestOptions?): EmbeddingResponse {
        return requester.perform {
            it.post {
                url(path = ApiPath.Embeddings)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }
}
