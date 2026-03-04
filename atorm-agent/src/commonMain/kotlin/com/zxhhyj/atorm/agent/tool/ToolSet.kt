package com.zxhhyj.atorm.agent.tool

public class ToolSet private constructor(tools: List<Tool<*, *>> = emptyList()) {

    private val _tools: MutableList<Tool<*, *>> = tools.toMutableList()

    public val tools: List<Tool<*, *>>
        get() = _tools.toList()

    public fun getToolOrNull(toolName: String): Tool<*, *>? {
        return _tools.firstOrNull { it.name == toolName }
    }

    public fun getTool(toolName: String): Tool<*, *> {
        return getToolOrNull(toolName)
            ?: throw IllegalArgumentException("Tool \"$toolName\" is not defined")
    }

    public inline fun <reified T : Tool<*, *>> getTool(): T {
        return tools
            .firstOrNull { it::class == T::class }
            ?.let { it as? T }
            ?: throw IllegalArgumentException("Tool with type ${T::class} is not defined")
    }

    public operator fun plus(toolSet: ToolSet): ToolSet {
        val mergedTools = (this.tools + toolSet.tools).distinctBy { it.name }
        return ToolSet(mergedTools)
    }

    public fun add(tool: Tool<*, *>) {
        if (_tools.contains(tool)) return
        _tools.add(tool)
    }

    public fun addAll(vararg tools: Tool<*, *>) {
        tools.forEach { tool -> add(tool) }
    }

    public class Builder internal constructor() {
        private val tools = mutableListOf<Tool<*, *>>()

        public fun tool(tool: Tool<*, *>) {
            require(tool.name !in tools.map { it.name }) { "Tool \"${tool.name}\" is already defined" }
            tools.add(tool)
        }

        public fun tools(toolsList: List<Tool<*, *>>) {
            toolsList.forEach { tool(it) }
        }

        public fun build(): ToolSet {
            return ToolSet(tools)
        }
    }

    public companion object {

        public fun builder(): ToolSetBuilder = ToolSetBuilder()

        public operator fun invoke(init: Builder.() -> Unit): ToolSet = Builder().apply(init).build()

        public val EMPTY: ToolSet = ToolSet(emptyList())
    }
}
