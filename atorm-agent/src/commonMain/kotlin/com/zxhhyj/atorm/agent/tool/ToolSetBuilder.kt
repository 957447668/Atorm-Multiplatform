package com.zxhhyj.atorm.agent.tool

public class ToolSetBuilder {
    private val builder = ToolSet.Builder()

    public fun tool(tool: Tool<*, *>): ToolSetBuilder = apply { builder.tool(tool) }

    public fun tools(toolsList: List<Tool<*, *>>): ToolSetBuilder = apply { builder.tools(toolsList) }

    public fun build(): ToolSet = builder.build()
}