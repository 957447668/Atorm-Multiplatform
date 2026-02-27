package com.zxhhyj.atorm

import com.zxhhyj.atorm.clients.LLMClient
import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.Prompt
import com.zxhhyj.atorm.core.prompt.message.Message
import com.zxhhyj.atorm.core.prompt.message.ResponseMetaInfo
import com.zxhhyj.atorm.core.prompt.streaming.ModerationResult
import com.zxhhyj.atorm.core.prompt.streaming.StreamFrame
import com.zxhhyj.atorm.core.tool.ToolDescriptor
import com.zxhhyj.atorm.openai.api.chat.ChatCompletionRequest
import com.zxhhyj.atorm.openai.api.chat.ChatMessage
import com.zxhhyj.atorm.openai.api.chat.ChatResponseFormat
import com.zxhhyj.atorm.openai.api.chat.ChatRole
import com.zxhhyj.atorm.openai.api.chat.FunctionCall
import com.zxhhyj.atorm.openai.api.chat.FunctionTool
import com.zxhhyj.atorm.openai.api.chat.JsonSchema
import com.zxhhyj.atorm.openai.api.chat.Tool
import com.zxhhyj.atorm.openai.api.chat.ToolCall
import com.zxhhyj.atorm.openai.api.chat.ToolId
import com.zxhhyj.atorm.openai.api.chat.ToolType
import com.zxhhyj.atorm.openai.api.core.Parameters
import com.zxhhyj.atorm.openai.api.model.ModelId
import com.zxhhyj.atorm.openai.client.OpenAI
import com.zxhhyj.atorm.openai.client.OpenAIConfig
import com.zxhhyj.atorm.openai.client.OpenAIHost
import com.zxhhyj.atrom.openai.utils.OpenAICompatibleToolDescriptorSchemaGenerator
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.serialization.json.buildJsonObject
import kotlin.time.Clock

public class DoubaoLLMClient(
    apiKey: String,
    settings: DoubaoClientSettings = DoubaoClientSettings(),
    baseClient: HttpClient = HttpClient(),
    clock: Clock = Clock.System
) : LLMClient {
    private val openAI = OpenAI(
        OpenAIConfig(
            token = apiKey,
            timeout = settings.timeout,
            host = OpenAIHost(settings.baseUrl),
            engine = baseClient.engine
        )
    )

    private fun buildChatCompletionRequest(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ) =
        ChatCompletionRequest(
            model = ModelId(model.id),
            messages = prompt.messages.map {
                when (it) {
                    is Message.System -> {
                        ChatMessage(
                            role = ChatRole.System,
                            content = it.content
                        )
                    }

                    is Message.User -> {
                        ChatMessage(
                            role = ChatRole.User,
                            content = it.content
                        )
                    }

                    is Message.Assistant -> {
                        ChatMessage(
                            role = ChatRole.Assistant,
                            content = it.content
                        )
                    }

                    is Message.Reasoning -> {
                        TODO()
                    }

                    is Message.Tool.Call -> {
                        ChatMessage.Assistant(
                            toolCalls = listOf(
                                ToolCall.Function(
                                    id = ToolId(it.id!!),
                                    function = FunctionCall(
                                        nameOrNull = it.tool,
                                        argumentsOrNull = it.content
                                    )
                                )
                            )
                        )
                    }

                    is Message.Tool.Result -> {
                        ChatMessage(
                            role = ChatRole.Tool,
                            toolCallId = ToolId(it.id!!),
                            content = it.content
                        )
                    }
                }
            },
            tools = tools.map { tool ->
                Tool(
                    type = ToolType.Function,
                    FunctionTool(
                        name = tool.name,
                        description = tool.description,
                        parameters = Parameters(
                            OpenAICompatibleToolDescriptorSchemaGenerator.generate(
                                tool
                            )
                        )
                    )
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
        val chatCompletion = openAI.chatCompletion(
            buildChatCompletionRequest(prompt, model, tools),
            buildJsonObject {
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

    override fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): Flow<StreamFrame> {
        return flow {
            var lastFrame: StreamFrame? = null
            var lastTooCallIndex: Int? = null
            openAI.chatCompletions(
                buildChatCompletionRequest(prompt, model, tools),
                buildJsonObject {
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
                            } ?: StreamFrame.ToolCall(
                                id.toString(),
                                name.toString(),
                                content.toString()
                            )
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