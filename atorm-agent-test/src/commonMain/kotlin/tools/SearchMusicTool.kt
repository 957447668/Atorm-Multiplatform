package tools

import com.zxhhyj.atorm.agent.tool.Tool
import com.zxhhyj.atorm.clients.LLMDescription
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

public object SearchMusicTool : Tool<SearchMusicTool.Args, String>(
    argsSerializer = Args.serializer(),
    resultSerializer = String.serializer(),
    name = "SearchMusicTool",
    description = "音乐搜索工具"
) {

    @Serializable
    public data class Args(@property:LLMDescription("音乐关键词") val query: String)

    override suspend fun execute(args: Args): String {
        return "找到如下歌曲: 采茶纪、终身误、行香子、单向箭头、月出、太初"
    }
}