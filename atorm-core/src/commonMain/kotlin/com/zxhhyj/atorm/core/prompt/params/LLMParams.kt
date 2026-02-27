package com.zxhhyj.atorm.core.prompt.params

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
public data class LLMParams(
    public val temperature: Double? = null,
    public val maxTokens: Int? = null,
    public val numberOfChoices: Int? = null,
    public val schema: Schema? = null,
    public val toolChoice: ToolChoice? = null,
    public val user: String? = null,
    public val additionalProperties: Map<String, JsonElement>? = null,
) {
    init {
        temperature?.let { temp ->
            require(temp in 0.0..2.0) { "Temperature must be between 0.0 and 2.0, but was $temp" }
        }
        numberOfChoices?.let { choices ->
            require(choices > 0) { "Number of choices must be greater than 0, but was $choices" }
        }
        user?.let { userId ->
            require(userId.isNotBlank()) { "User must not be empty or blank" }
        }
        toolChoice?.let { choice ->
            if (choice is ToolChoice.Named) {
                require(choice.name.isNotBlank()) { "Tool choice name must not be empty or blank" }
            }
        }
    }

    @Serializable
    public data class Schema(
        public val name: String,
        public val schema: JsonObject
    ) {
        init {
            require(name.isNotBlank()) { "Schema name must not be empty or blank" }
        }
    }

    @Serializable
    public sealed class ToolChoice {
        @Serializable
        public data class Named(val name: String) : ToolChoice() {
            init {
                require(name.isNotBlank()) { "Tool choice name must not be empty or blank" }
            }
        }

        @Serializable
        public object None : ToolChoice()

        @Serializable
        public object Auto : ToolChoice()

        @Serializable
        public object Required : ToolChoice()
    }
}