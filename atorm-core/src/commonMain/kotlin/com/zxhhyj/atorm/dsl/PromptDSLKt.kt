package com.zxhhyj.atorm.dsl

import com.zxhhyj.atorm.LLMParams
import com.zxhhyj.atorm.Prompt
import com.zxhhyj.atorm.PromptBuilder
import kotlin.time.Clock

@PromptDSL
public fun prompt(
    params: LLMParams = LLMParams(),
    clock: Clock = Clock.System,
    build: PromptBuilder.() -> Unit
): Prompt {
    return Prompt.build(params, clock, build)
}

@PromptDSL
public fun emptyPrompt(): Prompt = Prompt.build { }

public fun prompt(
    existing: Prompt,
    clock: Clock = Clock.System,
    build: PromptBuilder.() -> Unit
): Prompt {
    return Prompt.build(existing, clock, build)
}
