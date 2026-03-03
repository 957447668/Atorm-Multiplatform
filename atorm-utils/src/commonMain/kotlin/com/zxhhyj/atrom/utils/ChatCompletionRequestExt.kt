package com.zxhhyj.atrom.utils

import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.Prompt
import com.zxhhyj.atorm.core.prompt.message.Message
import com.zxhhyj.atorm.core.prompt.params.LLMParams
import com.zxhhyj.atorm.core.tool.ToolDescriptor
import com.zxhhyj.atorm.openai.api.chat.ChatCompletionRequest
import com.zxhhyj.atorm.openai.api.chat.ChatMessage
import com.zxhhyj.atorm.openai.api.chat.ChatResponseFormat
import com.zxhhyj.atorm.openai.api.chat.ChatRole
import com.zxhhyj.atorm.openai.api.chat.FunctionCall
import com.zxhhyj.atorm.openai.api.chat.FunctionTool
import com.zxhhyj.atorm.openai.api.chat.JsonSchema
import com.zxhhyj.atorm.openai.api.chat.TextContent
import com.zxhhyj.atorm.openai.api.chat.Tool
import com.zxhhyj.atorm.openai.api.chat.ToolCall
import com.zxhhyj.atorm.openai.api.chat.ToolChoice
import com.zxhhyj.atorm.openai.api.chat.ToolId
import com.zxhhyj.atorm.openai.api.chat.ToolType
import com.zxhhyj.atorm.openai.api.core.Parameters
import com.zxhhyj.atorm.openai.api.model.ModelId

public fun buildChatCompletionRequest(
    prompt: Prompt,
    model: LLModel,
    tools: List<ToolDescriptor>
): ChatCompletionRequest {
    return ChatCompletionRequest(
        model = ModelId(model.id),
        temperature = prompt.params.temperature,
        maxTokens = prompt.params.maxTokens,
        n = prompt.params.numberOfChoices,
        toolChoice = prompt.params.toolChoice?.let {
            when (it) {
                LLMParams.ToolChoice.Auto -> {
                    ToolChoice.Auto
                }

                is LLMParams.ToolChoice.Named -> {
                    ToolChoice.function(it.name)
                }

                LLMParams.ToolChoice.None -> {
                    ToolChoice.None
                }

                LLMParams.ToolChoice.Required -> {
                    ToolChoice.Required
                }
            }
        },
        user = prompt.params.user,
        messages = buildList {
            val deque = ArrayDeque(prompt.messages)
            while (deque.isNotEmpty()) {
                when (val current = deque.removeFirstOrNull() ?: break) {
                    is Message.System -> {
                        add(
                            ChatMessage(
                                role = ChatRole.System,
                                messageContent = TextContent(current.content)
                            )
                        )
                    }

                    is Message.User -> {
                        add(
                            ChatMessage(
                                role = ChatRole.User,
                                messageContent = TextContent(current.content)
                            )
                        )
                    }

                    is Message.Assistant -> {
                        when (deque.firstOrNull()) {
                            is Message.Tool -> {
                                val toolCalls = buildList {
                                    while (deque.isNotEmpty()) {
                                        when (deque.first()) {
                                            is Message.Tool.Call -> {
                                                val current =
                                                    deque.removeFirst() as Message.Tool.Call
                                                val next =
                                                    deque.firstOrNull { it is Message.Tool.Result }
                                                        ?.let {
                                                            deque.removeFirst()
                                                        }
                                                        ?: error("Missing Tool.Result for Tool.Call id=${current.id}")
                                                add(current to next)
                                            }

                                            else -> {
                                                break
                                            }
                                        }
                                    }
                                }
                                add(
                                    ChatMessage(
                                        role = ChatRole.Assistant,
                                        messageContent = TextContent(current.content),
                                        toolCalls = toolCalls.map { it.first }.map {
                                            ToolCall.Function(
                                                id = ToolId(it.id!!),
                                                function = FunctionCall(
                                                    nameOrNull = it.tool,
                                                    argumentsOrNull = it.content
                                                )
                                            )
                                        }
                                    ))
                                addAll(toolCalls.map { (tool, result) ->
                                    ChatMessage(
                                        role = ChatRole.Tool,
                                        toolCallId = ToolId(tool.id!!),
                                        name = tool.tool,
                                        messageContent = TextContent(result.content)
                                    )
                                })
                            }

                            else -> {
                                add(
                                    ChatMessage(
                                        role = ChatRole.Assistant,
                                        messageContent = TextContent(current.content)
                                    )
                                )
                            }
                        }
                    }

                    is Message.Reasoning -> {
                        throw NotImplementedError()
                    }

                    is Message.Tool.Call, is Message.Tool.Result -> {
                        error("Unexpected standalone Tool message: $current")
                    }
                }
            }
        },
        tools = tools.map { tool ->
            Tool(
                type = ToolType.Function,
                function = FunctionTool(
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
}
