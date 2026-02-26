package tools

import com.zxhhyj.atorm.agent.tool.Tool
import com.zxhhyj.atorm.clients.LLMDescription
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

object SearchMusicTool : Tool<SearchMusicTool.Args, String>(
    argsSerializer = Args.serializer(),
    resultSerializer = String.serializer(),
    name = "SearchMusicTool",
    description = "音乐搜索工具"
) {

    @Serializable
    data class Args(@property:LLMDescription("音乐关键词") val query: String)

    override suspend fun execute(args: Args): String {
        return "已搜索音乐: ${args.query}"
    }
}