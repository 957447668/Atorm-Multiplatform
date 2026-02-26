package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.core.ListResponse
import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.api.model.Model
import com.zxhhyj.atorm.openai.api.model.ModelId
import com.zxhhyj.atorm.openai.client.Models
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Models] API.
 */
internal class ModelsApi(private val requester: HttpRequester) : Models {

    override suspend fun models(requestOptions: RequestOptions?): List<Model> {
        return requester.perform<ListResponse<Model>> {
            it.get {
                url(path = ApiPath.Models)
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun model(modelId: ModelId, requestOptions: RequestOptions?): Model {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Models}/${modelId.id}")
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }
}
