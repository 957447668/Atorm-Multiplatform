package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.edits.Edit
import com.zxhhyj.atorm.openai.api.edits.EditsRequest
import com.zxhhyj.atorm.openai.client.Edits
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Edits]
 */
internal class EditsApi(private val requester: HttpRequester) : Edits {

    @Deprecated("Edits is deprecated. Chat completions is the recommend replacement.")
    override suspend fun edit(request: EditsRequest): Edit {
        return requester.perform {
            it.post {
                url(path = ApiPath.Edits)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }
}
