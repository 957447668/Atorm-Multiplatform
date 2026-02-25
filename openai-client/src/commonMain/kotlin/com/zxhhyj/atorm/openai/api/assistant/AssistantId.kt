package com.zxhhyj.atorm.openai.api.assistant

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * ID of an assistant.
 */
@Serializable
@JvmInline
public value class AssistantId(public val id: String)
