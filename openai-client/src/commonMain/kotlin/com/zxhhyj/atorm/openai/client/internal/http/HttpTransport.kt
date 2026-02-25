package com.zxhhyj.atorm.openai.client.internal.http

import com.zxhhyj.atorm.openai.api.exception.AuthenticationException
import com.zxhhyj.atorm.openai.api.exception.GenericIOException
import com.zxhhyj.atorm.openai.api.exception.InvalidRequestException
import com.zxhhyj.atorm.openai.api.exception.OpenAIAPIException
import com.zxhhyj.atorm.openai.api.exception.OpenAIError
import com.zxhhyj.atorm.openai.api.exception.OpenAIHttpException
import com.zxhhyj.atorm.openai.api.exception.OpenAIServerException
import com.zxhhyj.atorm.openai.api.exception.OpenAITimeoutException
import com.zxhhyj.atorm.openai.api.exception.PermissionException
import com.zxhhyj.atorm.openai.api.exception.RateLimitException
import com.zxhhyj.atorm.openai.api.exception.UnknownAPIException
import com.zxhhyj.atorm.openai.api.run.AssistantStreamEvent
import com.zxhhyj.atorm.openai.client.extension.toAssistantStreamEvent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.sse.sseSession
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.sse.ServerSentEvent
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException

/** HTTP transport layer */
internal class HttpTransport(private val httpClient: HttpClient) : HttpRequester {

    /** Perform an HTTP request and get a result */
    override suspend fun <T : Any> perform(info: TypeInfo, block: suspend (HttpClient) -> HttpResponse): T {
        try {
            val response = block(httpClient)
            return response.body(info)
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    override suspend fun <T : Any> perform(
        builder: HttpRequestBuilder,
        block: suspend (response: HttpResponse) -> T
    ) {
        try {
            HttpStatement(builder = builder, client = httpClient).execute(block)
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    override suspend fun performSse(
        builderBlock: HttpRequestBuilder.() -> Unit
    ): Flow<AssistantStreamEvent> {
        try {
            return httpClient
                .sseSession(block = builderBlock)
                .incoming
                .map(ServerSentEvent::toAssistantStreamEvent)
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    override fun close() {
        httpClient.close()
    }

    /**
     * Handles various exceptions that can occur during an API request and converts them into appropriate
     * [com.zxhhyj.atorm.openai.api.exception.OpenAIException] instances.
     */
    private suspend fun handleException(e: Throwable) = when (e) {
        is CancellationException -> e // propagate coroutine cancellation
        is ClientRequestException -> openAIAPIException(e)
        is ServerResponseException -> OpenAIServerException(e)
        is HttpRequestTimeoutException, is SocketTimeoutException, is ConnectTimeoutException -> OpenAITimeoutException(
            e
        )

        is IOException -> GenericIOException(e)
        else -> OpenAIHttpException(e)
    }

    /**
     * Converts a [io.ktor.client.plugins.ClientRequestException] into a corresponding [com.zxhhyj.atorm.openai.api.exception.OpenAIAPIException] based on the HTTP status code.
     * This function helps in handling specific API errors and categorizing them into appropriate exception classes.
     */
    private suspend fun openAIAPIException(exception: ClientRequestException): OpenAIAPIException {
        val response = exception.response
        val status = response.status.value
        val error = response.body<OpenAIError>()
        return when (status) {
            429 -> RateLimitException(status, error, exception)
            400, 404, 409, 415 -> InvalidRequestException(status, error, exception)
            401 -> AuthenticationException(status, error, exception)
            403 -> PermissionException(status, error, exception)
            else -> UnknownAPIException(status, error, exception)
        }
    }
}
