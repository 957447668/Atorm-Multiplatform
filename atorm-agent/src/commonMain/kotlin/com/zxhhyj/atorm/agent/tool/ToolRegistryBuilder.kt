package com.zxhhyj.atorm.agent.tool

/**
 * A builder class for creating a `ToolRegistry` instance. This class provides methods to configure
 * and register tools, either individually or as a list, and then constructs a registry containing
 * the defined tools.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public class ToolRegistryBuilder {
    private val builder = ToolRegistry.Builder()

    public fun tool(tool: Tool<*, *>): ToolRegistryBuilder = apply { builder.tool(tool) }

    public fun tools(toolsList: List<Tool<*, *>>): ToolRegistryBuilder = apply { builder.tools(toolsList) }

    public fun build(): ToolRegistry = builder.build()
}
