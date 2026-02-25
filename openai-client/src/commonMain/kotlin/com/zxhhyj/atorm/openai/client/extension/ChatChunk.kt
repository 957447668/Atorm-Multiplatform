package com.zxhhyj.atorm.openai.client.extension

import com.zxhhyj.atorm.openai.api.chat.ChatChunk
import com.zxhhyj.atorm.openai.api.chat.ChatMessage
import com.zxhhyj.atorm.openai.client.extension.internal.ChatMessageAssembler

/**
 * Merges a list of [com.zxhhyj.atorm.openai.api.chat.ChatChunk]s into a single consolidated [com.zxhhyj.atorm.openai.api.chat.ChatMessage].
 */
public fun List<ChatChunk>.mergeToChatMessage(): ChatMessage {
    require(isNotEmpty()) { "ChatChunks List must not be empty" }
    return fold(ChatMessageAssembler()) { assembler, chatChunk -> assembler.merge(chatChunk) }.build()
}
