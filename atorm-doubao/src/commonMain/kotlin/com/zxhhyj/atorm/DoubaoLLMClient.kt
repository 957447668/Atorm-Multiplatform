package com.zxhhyj.atorm

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionTool
import com.aallam.openai.api.chat.JsonSchema
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.chat.ToolType
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.zxhhyj.atorm.clients.LLMClient
import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.Prompt
import com.zxhhyj.atorm.core.prompt.message.Message
import com.zxhhyj.atorm.core.prompt.message.ResponseMetaInfo
import com.zxhhyj.atorm.core.prompt.streaming.ModerationResult
import com.zxhhyj.atorm.core.prompt.streaming.StreamFrame
import com.zxhhyj.atorm.core.tool.ToolDescriptor
import com.zxhhyj.atorm.core.tool.ToolParameterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.serialization.json.addAll
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

public class DoubaoLLMClient(apiKey: String, clock: Clock = Clock.System) : LLMClient {
    private val openAI = OpenAI(
        OpenAIConfig(
            token = apiKey,
            timeout = Timeout(socket = 60.seconds),
            host = OpenAIHost("https://ark.cn-beijing.volces.com/api/v3/")
        )
    )

    private fun buildChatCompletionRequest(prompt: Prompt, model: LLModel, tools: List<ToolDescriptor>) =
        ChatCompletionRequest(
            model = ModelId(model.id),
            messages = prompt.messages.map {
                val role = when (it.role) {
                    Message.Role.System -> ChatRole.System
                    Message.Role.User -> ChatRole.User
                    Message.Role.Assistant -> ChatRole.Assistant
                    Message.Role.Reasoning -> TODO()
                    Message.Role.Tool -> ChatRole.Tool
                }
                ChatMessage(
                    role = role,
                    content = it.content
                )
            },
            tools = tools.map { tool ->
                Tool(
                    type = ToolType.Function,
                    FunctionTool(
                        name = tool.name,
                        description = tool.description,
                        parameters = Parameters.buildJsonObject {
                            put("type", "object")
                            putJsonObject("properties") {
                                (tool.requiredParameters + tool.optionalParameters).forEach {
                                    putJsonObject(it.name) {
                                        put(
                                            "type", when (it.type) {
                                                ToolParameterType.Boolean -> "boolean"
                                                is ToolParameterType.Enum -> "enum"
                                                ToolParameterType.Integer -> "integer"
                                                is ToolParameterType.List -> "array"
                                                ToolParameterType.Null -> "null"
                                                is ToolParameterType.Object -> "object"
                                                ToolParameterType.String -> "string"
                                                else -> TODO()
                                            }
                                        )
                                        put("description", it.description)
                                    }
                                }
                            }
                            putJsonArray("required") {
                                addAll(tool.requiredParameters.map { it.name })
                            }
                        })
                )
            },
            responseFormat = prompt.params.schema?.let {
                ChatResponseFormat.jsonSchema(
                    JsonSchema(
                        name = it.name,
                        schema = it.schema,
                        strict = true
                    )
                )
            }
        )

    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): List<Message.Response> {
        val chatCompletion = openAI.chatCompletion(buildChatCompletionRequest(prompt, model, tools), buildJsonObject {
            prompt.params.additionalProperties?.forEach {
                put(it.key, it.value)
            }
        })
        return buildList {
            chatCompletion.choices.forEach { choice ->
                choice.message.reasoningContent?.let {
                    add(Message.Reasoning(content = it, metaInfo = ResponseMetaInfo.Empty))
                }
                choice.message.content?.let {
                    add(Message.Assistant(content = it, metaInfo = ResponseMetaInfo.Empty))
                }
                choice.message.toolCalls?.filterIsInstance<ToolCall.Function>()?.forEach {
                    add(
                        Message.Tool.Call(
                            id = it.id.id,
                            tool = it.function.name,
                            content = it.function.arguments,
                            metaInfo = ResponseMetaInfo.Empty
                        )
                    )
                }
            }
        }
    }

    override fun executeStreaming(prompt: Prompt, model: LLModel, tools: List<ToolDescriptor>): Flow<StreamFrame> {
        return flow {
            var lastFrame: StreamFrame? = null
            var lastTooCallIndex: Int? = null
            openAI.chatCompletions(buildChatCompletionRequest(prompt, model, tools), buildJsonObject {
                prompt.params.additionalProperties?.forEach {
                    put(it.key, it.value)
                }
            }).onCompletion {
                lastFrame?.let {
                    emit(it)
                }
            }.collect {
                it.choices.forEach { chunk ->
                    when {
                        chunk.delta?.reasoningContent?.isNotEmpty() == true -> {}

                        chunk.delta?.content?.isNotEmpty() == true -> {
                            emit(StreamFrame.Append(chunk.delta!!.content!!))
                            lastFrame = null
                        }

                        chunk.delta?.toolCalls?.isNotEmpty() == true -> {
                            val id = StringBuilder()
                            val name = StringBuilder()
                            val content = StringBuilder()
                            chunk.delta!!.toolCalls!!.forEach { callChunk ->
                                if (lastTooCallIndex != null && lastTooCallIndex != callChunk.index) {
                                    emit(lastFrame!!)
                                    lastFrame = null
                                    lastTooCallIndex = null
                                } else {
                                    lastTooCallIndex = callChunk.index
                                }
                                callChunk.id?.let {
                                    id.append(it.id)
                                }
                                callChunk.function?.nameOrNull?.let { name.append(it) }
                                callChunk.function?.argumentsOrNull?.let { content.append(it) }
                            }

                            lastFrame = (lastFrame as StreamFrame.ToolCall?)?.let {
                                StreamFrame.ToolCall(
                                    it.id + id.toString(),
                                    it.name + name.toString(),
                                    it.content + content.toString()
                                )
                            } ?: StreamFrame.ToolCall(id.toString(), name.toString(), content.toString())
                        }
                    }
                }
            }
        }
    }

    override suspend fun moderate(
        prompt: Prompt,
        model: LLModel
    ): ModerationResult {
        TODO("Not yet implemented")
    }

    override fun close() {
        openAI.close()
    }
}