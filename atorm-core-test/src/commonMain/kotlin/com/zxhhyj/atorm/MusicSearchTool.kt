package com.zxhhyj.atorm

object MusicSearchTool : ToolDescriptor(
    name = "MusicSearchTool",
    description = "音乐搜索工具",
    requiredParameters = listOf(
        ToolParameterDescriptor(
            name = "query",
            description = "搜索关键词",
            type = ToolParameterType.String
        )
    )
)