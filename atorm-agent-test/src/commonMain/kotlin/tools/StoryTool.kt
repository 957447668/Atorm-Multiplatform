package tools

import com.zxhhyj.atorm.agent.tool.Tool
import com.zxhhyj.atorm.clients.LLMDescription
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

public object StoryTool : Tool<StoryTool.Args, String>(
    argsSerializer = Args.serializer(),
    resultSerializer = String.serializer(),
    name = "StoryTool",
    description = "故事生成工具"
) {

    @Serializable
    public data class Args(@property:LLMDescription("故事主题") val topic: String)

    override suspend fun execute(args: Args): String {
        return "已生成故事: ${args.topic}"
    }
}