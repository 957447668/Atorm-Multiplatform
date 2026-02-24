package com.aallam.openai.api.batch

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The batch identifier.
 */
@JvmInline
@Serializable
public value class BatchId(public val id: String)
