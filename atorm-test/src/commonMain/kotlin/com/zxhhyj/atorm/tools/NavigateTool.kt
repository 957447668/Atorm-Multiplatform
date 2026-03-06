package com.zxhhyj.atorm.tools

import com.zxhhyj.atorm.agent.tool.Tool
import com.zxhhyj.atorm.clients.LLMDescription
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

object NavigateTool : Tool<NavigateTool.Args, String>(
    argsSerializer = Args.serializer(),
    resultSerializer = String.serializer(),
    name = "NavigateTool",
    description = "导航工具"
) {

    @Serializable
    data class Args(@property:LLMDescription("目的地关键词") val query: String)

    override suspend fun execute(args: Args): String {
        return "已导航至: ${args.query}"
    }
}