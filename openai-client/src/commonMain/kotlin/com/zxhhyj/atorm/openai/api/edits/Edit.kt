package com.zxhhyj.atorm.openai.api.edits

import com.zxhhyj.atorm.openai.api.completion.Choice
import com.zxhhyj.atorm.openai.api.core.Usage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response to the edit creation request.
 */
@Serializable
public class Edit(
    /**
     * The creation time in epoch milliseconds.
     */
    @SerialName("created") public val created: Long,

    /**
     * A list of generated completions.
     */
    @SerialName("choices") public val choices: List<Choice>,

    /**
     * Edit usage data.
     */
    @SerialName("usage") public val usage: Usage,
)
