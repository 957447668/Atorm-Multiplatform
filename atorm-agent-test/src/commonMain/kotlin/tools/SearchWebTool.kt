package tools

import com.zxhhyj.atorm.agent.tool.Tool
import com.zxhhyj.atorm.clients.LLMDescription
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

public object SearchWebTool : Tool<SearchWebTool.Args, String>(
    argsSerializer = Args.serializer(),
    resultSerializer = String.serializer(),
    name = "SearchWebTool",
    description = "网络搜索工具"
) {

    @Serializable
    public data class Args(@property:LLMDescription("搜索关键词") val query: String)

    override suspend fun execute(args: Args): String {
        return "已搜索网络: ${args.query}"
    }
}