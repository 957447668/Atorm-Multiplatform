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
