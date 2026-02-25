package com.zxhhyj.atorm

object VideoSearchTool : ToolDescriptor(
    name = "VideoSearchTool",
    description = "视频搜索工具",
    requiredParameters = listOf(
        ToolParameterDescriptor(
            name = "query",
            description = "搜索关键词",
            type = ToolParameterType.String
        )
    )
)