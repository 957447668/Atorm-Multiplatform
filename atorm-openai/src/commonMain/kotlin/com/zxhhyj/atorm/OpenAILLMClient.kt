package com.zxhhyj.atorm

import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

public class OpenAILLMClient(apiKey: String, clock: Clock = Clock.System) : LLMClient {

    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): List<Message.Response> {
        TODO("Not yet implemented")
    }

    override fun executeStreaming(prompt: Prompt, model: LLModel, tools: List<ToolDescriptor>): Flow<StreamFrame> {
        TODO("Not yet implemented")
    }

    override suspend fun moderate(
        prompt: Prompt,
        model: LLModel
    ): ModerationResult {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}