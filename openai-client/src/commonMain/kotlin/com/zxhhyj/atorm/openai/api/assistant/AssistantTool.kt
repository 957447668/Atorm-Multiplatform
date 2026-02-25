package com.zxhhyj.atorm.openai.api.assistant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A list of tool enabled on the assistant. There can be a maximum of 128 tools per assistant.
 * Tools can be of types code_interpreter, file_search, or function.
 */
@Serializable
public sealed interface AssistantTool {
    /**
     * The type of tool being defined: code_interpreter
     */
    @Serializable
    @SerialName("code_interpreter")
    public data object CodeInterpreter : AssistantTool

    /**
     * The type of tool being defined: file_search
     */
    @Serializable
    @SerialName("file_search")
    public data object FileSearch : AssistantTool

    /**
     * The type of tool being defined: function
     */
    @Serializable
    @SerialName("function")
    public data class FunctionTool(
        /**
         * The name of the function to be called.
         */
        @SerialName("function") public val function: Function,
    ) : AssistantTool

}
