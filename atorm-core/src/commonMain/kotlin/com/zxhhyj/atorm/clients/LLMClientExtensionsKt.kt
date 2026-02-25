package com.zxhhyj.atorm.clients

import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.Prompt

public expect suspend inline fun <reified T> LLMClient.executeStructured(
    prompt: Prompt,
    model: LLModel,
    examples: List<T> = emptyList(),
): T