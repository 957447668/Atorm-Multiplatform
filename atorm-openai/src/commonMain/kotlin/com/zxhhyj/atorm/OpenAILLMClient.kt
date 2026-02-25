package com.zxhhyj.atorm

import com.zxhhyj.atorm.clients.LLMClient
import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.Prompt
import com.zxhhyj.atorm.core.prompt.message.Message
import com.zxhhyj.atorm.core.prompt.streaming.ModerationResult
import com.zxhhyj.atorm.core.prompt.streaming.StreamFrame
import com.zxhhyj.atorm.core.tool.ToolDescriptor
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