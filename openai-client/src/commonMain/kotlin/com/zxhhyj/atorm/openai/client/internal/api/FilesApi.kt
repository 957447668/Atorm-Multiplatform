package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.core.DeleteResponse
import com.zxhhyj.atorm.openai.api.core.ListResponse
import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.api.exception.OpenAIAPIException
import com.zxhhyj.atorm.openai.api.file.File
import com.zxhhyj.atorm.openai.api.file.FileId
import com.zxhhyj.atorm.openai.api.file.FileUpload
import com.zxhhyj.atorm.openai.client.Files
import com.zxhhyj.atorm.openai.client.internal.extension.appendFileSource
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

/**
 * Implementation of [Files].
 */
internal class FilesApi(private val requester: HttpRequester) : Files {

    override suspend fun file(request: FileUpload, requestOptions: RequestOptions?): File {
        return requester.perform {
            it.submitFormWithBinaryData(url = ApiPath.Files, formData = formData {
                appendFileSource("file", request.file)
                append(key = "purpose", value = request.purpose.raw)
            }) {
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun files(requestOptions: RequestOptions?): List<File> {
        return requester.perform<ListResponse<File>> {
            it.get {
                url(path = ApiPath.Files)
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun file(fileId: FileId, requestOptions: RequestOptions?): File? {
        try {
            return requester.perform<HttpResponse> {
                it.get {
                    url(path = "${ApiPath.Files}/${fileId.id}")
                    requestOptions(requestOptions)
                }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun delete(fileId: FileId, requestOptions: RequestOptions?): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.Files}/${fileId.id}")
                requestOptions(requestOptions)
            }
        }

        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }

    override suspend fun download(fileId: FileId, requestOptions: RequestOptions?): ByteArray {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Files}/${fileId.id}/content")
                requestOptions(requestOptions)
            }
        }
    }

}
