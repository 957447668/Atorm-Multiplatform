package com.zxhhyj.atorm.clients

import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.Prompt
import com.zxhhyj.atorm.core.prompt.message.LLMChoice
import com.zxhhyj.atorm.core.prompt.message.Message
import com.zxhhyj.atorm.core.prompt.streaming.ModerationResult
import com.zxhhyj.atorm.core.prompt.streaming.StreamFrame
import com.zxhhyj.atorm.core.tool.ToolDescriptor
import kotlinx.coroutines.flow.Flow

public interface LLMClient : AutoCloseable {
    public suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor> = emptyList()
    ): List<Message.Response>

    public fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor> = emptyList()
    ): Flow<StreamFrame> = error("Not implemented for this client")

    public suspend fun executeMultipleChoices(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor> = emptyList()
    ): List<LLMChoice> =
        throw UnsupportedOperationException("Not implemented for this client")

    public suspend fun moderate(prompt: Prompt, model: LLModel): ModerationResult

    public suspend fun models(): List<String> {
        throw UnsupportedOperationException("Not implemented for this client")
    }

    public val clientName: String
        get() = this::class.simpleName ?: "UnknownClient"
}

public data class ConnectionTimeoutConfig(
    val requestTimeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    val connectTimeoutMillis: Long = DEFAULT_CONNECT_TIMEOUT_MS,
    val socketTimeoutMillis: Long = DEFAULT_TIMEOUT_MS,
) {
    private companion object {
        private const val DEFAULT_TIMEOUT_MS: Long = 900000
        private const val DEFAULT_CONNECT_TIMEOUT_MS: Long = 60_000
    }
}
