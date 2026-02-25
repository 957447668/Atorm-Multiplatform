package com.zxhhyj.atorm.openai.api.run

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Details on the action required to continue the run.
 */
@Serializable
public sealed interface RequiredAction {

    @Serializable
    @SerialName("submit_tool_outputs")
    public class SubmitToolOutputs(
        /**
         * A list of the relevant tool calls.
         */
        @SerialName("submit_tool_outputs") public val toolOutputs: ToolOutputs,
    ) : RequiredAction
}
