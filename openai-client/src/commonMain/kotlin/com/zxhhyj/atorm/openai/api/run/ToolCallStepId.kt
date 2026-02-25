package com.zxhhyj.atorm.openai.api.run

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Tool call step identifier.
 */
@JvmInline
@Serializable
public value class ToolCallStepId(public val id: String)
