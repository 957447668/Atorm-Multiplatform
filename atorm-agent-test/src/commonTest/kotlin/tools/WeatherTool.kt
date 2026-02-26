package tools

import com.zxhhyj.atorm.agent.tool.Tool
import com.zxhhyj.atorm.clients.LLMDescription
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

object WeatherTool : Tool<WeatherTool.Args, String>(
    argsSerializer = Args.serializer(),
    resultSerializer = String.serializer(),
    name = "WeatherTool",
    description = "天气查询工具"
) {

    @Serializable
    data class Args(@property:LLMDescription("城市名称") val city: String)

    override suspend fun execute(args: Args): String {
        return "已查询天气: ${args.city}"
    }
}