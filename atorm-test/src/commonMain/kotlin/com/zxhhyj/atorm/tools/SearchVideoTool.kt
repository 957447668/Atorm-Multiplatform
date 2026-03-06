package com.zxhhyj.atorm.tools

import com.zxhhyj.atorm.agent.tool.Tool
import com.zxhhyj.atorm.clients.LLMDescription
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

object SearchVideoTool : Tool<SearchVideoTool.Args, String>(
    argsSerializer = Args.serializer(),
    resultSerializer = String.serializer(),
    name = "SearchViddeoTool",
    description = "视频搜索工具"
) {

    @Serializable
    data class Args(@property:LLMDescription("视频关键词") val query: String)

    override suspend fun execute(args: Args): String {
        return "已搜索视频: ${args.query}"
    }
}