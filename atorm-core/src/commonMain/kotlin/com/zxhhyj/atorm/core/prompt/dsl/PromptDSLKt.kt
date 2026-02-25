package com.zxhhyj.atorm.core.prompt.dsl

import com.zxhhyj.atorm.core.prompt.params.LLMParams
import com.zxhhyj.atorm.core.prompt.Prompt
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
