package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.core.PaginatedList
import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.api.core.SortOrder
import com.zxhhyj.atorm.openai.api.run.AssistantStreamEvent
import com.zxhhyj.atorm.openai.api.run.Run
import com.zxhhyj.atorm.openai.api.run.RunId
import com.zxhhyj.atorm.openai.api.run.RunRequest
import com.zxhhyj.atorm.openai.api.run.RunStep
import com.zxhhyj.atorm.openai.api.run.RunStepId
import com.zxhhyj.atorm.openai.api.run.ThreadRunRequest
import com.zxhhyj.atorm.openai.api.run.ToolOutput
import com.zxhhyj.atorm.openai.api.thread.ThreadId
import com.zxhhyj.atorm.openai.client.Runs
import com.zxhhyj.atorm.openai.client.internal.extension.beta
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow

internal class RunsApi(val requester: HttpRequester) : Runs {
    override suspend fun createRun(threadId: ThreadId, request: RunRequest, requestOptions: RequestOptions?): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs")
                setBody(request.copy(stream = false))
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createStreamingRun(
        threadId: ThreadId,
        request: RunRequest,
        requestOptions: RequestOptions?
    ): Flow<AssistantStreamEvent> {
        return requester
            .performSse {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs")
                setBody(request.copy(stream = true))
                contentType(ContentType.Application.Json)
                accept(ContentType.Text.EventStream)
                beta("assistants", 2)
                requestOptions(requestOptions)
                method = HttpMethod.Post
            }
    }

    override suspend fun getRun(threadId: ThreadId, runId: RunId, requestOptions: RequestOptions?): Run {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}")
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateRun(
        threadId: ThreadId,
        runId: RunId,
        metadata: Map<String, String>?,
        requestOptions: RequestOptions?
    ): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}")
                metadata?.let { meta ->
                    setBody(mapOf("metadata" to meta))
                    contentType(ContentType.Application.Json)
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun runs(
        threadId: ThreadId,
        limit: Int?,
        order: SortOrder?,
        after: RunId?,
        before: RunId?,
        requestOptions: RequestOptions?
    ): PaginatedList<Run> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs") {
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

    override suspend fun submitToolOutput(
        threadId: ThreadId,
        runId: RunId,
        output: List<ToolOutput>,
        requestOptions: RequestOptions?
    ): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/submit_tool_outputs")
                setBody(mapOf("tool_outputs" to output))
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun submitStreamingToolOutput(
        threadId: ThreadId,
        runId: RunId,
        output: List<ToolOutput>,
        requestOptions: RequestOptions?
    ): Flow<AssistantStreamEvent> {
        return requester
            .performSse {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/submit_tool_outputs")
                setBody(mapOf("tool_outputs" to output, "stream" to true))
                contentType(ContentType.Application.Json)
                accept(ContentType.Text.EventStream)
                beta("assistants", 2)
                requestOptions(requestOptions)
                method = HttpMethod.Post
            }
    }

    override suspend fun cancel(threadId: ThreadId, runId: RunId, requestOptions: RequestOptions?): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/cancel")
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createThreadRun(request: ThreadRunRequest, requestOptions: RequestOptions?): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/runs")
                setBody(request.copy(stream = false))
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createStreamingThreadRun(
        request: ThreadRunRequest,
        requestOptions: RequestOptions?
    ): Flow<AssistantStreamEvent> {
        return requester
            .performSse {
                url(path = "${ApiPath.Threads}/runs")
                setBody(request.copy(stream = true))
                contentType(ContentType.Application.Json)
                accept(ContentType.Text.EventStream)
                beta("assistants", 2)
                requestOptions(requestOptions)
                method = HttpMethod.Post
            }
    }


    override suspend fun runStep(
        threadId: ThreadId,
        runId: RunId,
        stepId: RunStepId,
        requestOptions: RequestOptions?
    ): RunStep {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/steps/${stepId.id}")
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun runSteps(
        threadId: ThreadId,
        runId: RunId,
        limit: Int?,
        order: SortOrder?,
        after: RunStepId?,
        before: RunStepId?,
        requestOptions: RequestOptions?
    ): PaginatedList<RunStep> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/steps") {
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
