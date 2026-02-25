package com.zxhhyj.atorm

import com.zxhhyj.atorm.core.tool.ToolDescriptor
import com.zxhhyj.atorm.core.tool.ToolParameterDescriptor
import com.zxhhyj.atorm.core.tool.ToolParameterType

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