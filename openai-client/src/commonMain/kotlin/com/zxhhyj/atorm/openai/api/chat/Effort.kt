package com.zxhhyj.atorm.openai.api.chat

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Reasoning Effort.
 */
@Serializable
@JvmInline
public value class Effort(public val id: String)
