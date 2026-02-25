package com.zxhhyj.atorm.core.llm

import kotlinx.serialization.Serializable

@Serializable
public data class LLModel(
    val id: String,
    val contextLength: Long,
    val maxOutputTokens: Long? = null,
)