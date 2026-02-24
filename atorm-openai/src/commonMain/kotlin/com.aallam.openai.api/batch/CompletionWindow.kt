package com.aallam.openai.api.batch

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The batch identifier.
 */
@JvmInline
@Serializable
public value class CompletionWindow(public val value: String) {
    public companion object {
        public val TwentyFourHours: CompletionWindow = CompletionWindow("24h")
    }
}
