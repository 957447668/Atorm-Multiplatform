package com.zxhhyj.atorm

import kotlinx.coroutines.flow.Flow

public interface PromptExecutor : AutoCloseable {

    public suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor> = emptyList()
    ): List<Message.Response>

    public fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor> = emptyList()
    ): Flow<StreamFrame>

    public suspend fun executeMultipleChoices(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): List<LLMChoice> =
        listOf(execute(prompt, model, tools))

    public suspend fun models(): List<String> {
        throw UnsupportedOperationException("Not implemented for this executor")
    }
}
