package com.zxhhyj.atorm

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import kotlinx.coroutines.flow.collect
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

suspend fun main() {
    val config = OpenAIConfig(
        token = "528f58b3-68d6-4253-a19e-28e9bc6223e8",
        timeout = Timeout(socket = 60.seconds),
        host = OpenAIHost("https://ark.cn-beijing.volces.com/api/v3/"),
        headers = mapOf(
            "thinking" to """{"type": "disabled"}"""
        )
    )

    val openAI = OpenAI(config)

    val request = ChatCompletionRequest(
        model = ModelId("doubao-seed-1-6-flash-250828"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful assistant!"
            ),
            ChatMessage(
                role = ChatRole.User,
                content = "Hello!"
            )
        )
    )

    openAI.chatCompletions(request).collect()

    val startTime = TimeSource.Monotonic.markNow()
    var firstFrame = true
    openAI.chatCompletions(request).collect {
        it.choices.mapNotNull { it.delta?.content }.filter { it.isNotEmpty() }.forEach {
            if (firstFrame) {
                println("第一帧时间差: ${startTime.elapsedNow().inWholeMilliseconds}ms")
                firstFrame = false
            }
            println(it)
        }
    }
}