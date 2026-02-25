package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.api.moderation.ModerationRequest
import com.zxhhyj.atorm.openai.api.moderation.TextModeration
import com.zxhhyj.atorm.openai.client.Moderations
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Moderations].
 */
internal class ModerationsApi(private val requester: HttpRequester) : Moderations {

    override suspend fun moderations(request: ModerationRequest, requestOptions: RequestOptions?): TextModeration {
        return requester.perform {
            it.post {
                url(path = ApiPath.Moderations)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }
}
