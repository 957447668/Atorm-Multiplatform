package com.zxhhyj.atorm.openai.api.run

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * A run step id.
 */
@Serializable
@JvmInline
public value class RunStepId(public val id: String)
