package com.zxhhyj.atorm.core.prompt

import com.zxhhyj.atorm.core.prompt.dsl.PromptBuilder
import com.zxhhyj.atorm.core.prompt.message.Message
import com.zxhhyj.atorm.core.prompt.params.LLMParams
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration

@Serializable
public data class Prompt(
    val messages: List<Message>,
    val params: LLMParams = LLMParams()
) {
    public companion object {

        public val Empty: Prompt = Prompt(emptyList())

        public fun build(
            params: LLMParams = LLMParams(),
            clock: Clock = Clock.System,
            init: PromptBuilder.() -> Unit
        ): Prompt {
            val builder = PromptBuilder(params, clock)
            builder.init()
            return builder.build()
        }

        public fun build(prompt: Prompt, clock: Clock = Clock.System, init: PromptBuilder.() -> Unit): Prompt {
            return PromptBuilder.from(prompt, clock).also(init).build()
        }
    }

    public val latestTokenUsage: Int
        get() = messages
            .lastOrNull { it is Message.Response }
            ?.let { it as? Message.Response }
            ?.metaInfo?.totalTokensCount ?: 0

    public val totalTimeSpent: Duration
        get() = when {
            messages.isEmpty() -> Duration.ZERO
            else -> messages.last().metaInfo.timestamp - messages.first().metaInfo.timestamp
        }

    public fun withMessages(update: (List<Message>) -> List<Message>): Prompt =
        this.copy(messages = update(this.messages))

    public fun withParams(newParams: LLMParams): Prompt = copy(params = newParams)

    public class LLMParamsUpdateContext internal constructor(
        public var temperature: Double?,
        public var schema: LLMParams.Schema?,
        public var toolChoice: LLMParams.ToolChoice?,
        public var user: String? = null,
    ) {
        internal constructor(params: LLMParams) : this(
            params.temperature,
            params.schema,
            params.toolChoice,
            params.user,
        )

        public fun toParams(): LLMParams = LLMParams(
            temperature = temperature,
            schema = schema,
            toolChoice = toolChoice,
            user = user
        )

        internal fun applyToParams(params: LLMParams): LLMParams = params.copy(
            temperature = temperature,
            schema = schema,
            toolChoice = toolChoice,
            user = user,
        )
    }

    public fun withUpdatedParams(update: LLMParamsUpdateContext.() -> Unit): Prompt =
        copy(params = LLMParamsUpdateContext(params).apply { update() }.applyToParams(params))
}
