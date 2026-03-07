package com.zxhhyj.atorm.model

sealed interface ChatMessage {
    val id: String
    val timestamp: Long

    data class UserMessage(
        override val id: String,
        override val timestamp: Long,
        val text: String
    ) : ChatMessage

    sealed interface AiMessage : ChatMessage {
        data class TempAiMessage(
            override val id: String,
            override val timestamp: Long,
            val incrementalText: String
        ) : AiMessage

        private data class FinalAiMessage(
            override val id: String,
            override val timestamp: Long,
            val fullText: String
        ) : AiMessage
    }
}