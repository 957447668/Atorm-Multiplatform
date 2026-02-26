package com.zxhhyj.atorm.clients

import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.Prompt

public actual suspend inline fun <reified T> LLMClient.executeStructured(
    prompt: Prompt,
    model: LLModel,
    examples: List<T>
): T {
    TODO("Not yet implemented")
}