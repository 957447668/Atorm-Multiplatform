package com.zxhhyj.atorm

import com.zxhhyj.atorm.core.tool.ToolDescriptor
import com.zxhhyj.atorm.core.tool.ToolParameterDescriptor
import com.zxhhyj.atorm.core.tool.ToolParameterType

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