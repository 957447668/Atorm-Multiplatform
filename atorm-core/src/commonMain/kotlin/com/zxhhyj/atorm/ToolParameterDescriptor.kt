package com.zxhhyj.atorm

import kotlin.enums.EnumEntries

public data class ToolParameterDescriptor(
    val name: String,
    val description: String,
    val type: ToolParameterType
)

public sealed class ToolParameterType(public val name: kotlin.String) {

    public data object String : ToolParameterType("STRING")

    public data object Null : ToolParameterType("NULL")

    public data object Integer : ToolParameterType("INT")

    public data object Float : ToolParameterType("FLOAT")

    public data object Boolean : ToolParameterType("BOOLEAN")

    public data class Enum(val entries: Array<kotlin.String>) : ToolParameterType("ENUM") {
        override fun equals(other: Any?): kotlin.Boolean = other is Enum && this.entries.contentEquals(other.entries)
    }

    public data class List(val itemsType: ToolParameterType) : ToolParameterType("ARRAY")

    public data class AnyOf(val types: Array<ToolParameterDescriptor>) : ToolParameterType("ANYOF") {
        override fun equals(other: Any?): kotlin.Boolean = other is AnyOf && this.types.contentEquals(other.types)
        override fun hashCode(): Int = types.contentHashCode()
    }

    public data class Object(
        val properties: kotlin.collections.List<ToolParameterDescriptor>,
        val requiredProperties: kotlin.collections.List<kotlin.String> = listOf(),
        val additionalProperties: kotlin.Boolean? = null,
        val additionalPropertiesType: ToolParameterType? = null,
    ) : ToolParameterType("OBJECT")

    public companion object {
        public fun Enum(entries: EnumEntries<*>): Enum = Enum(entries.map { it.name }.toTypedArray())

        public fun Enum(entries: Array<kotlin.Enum<*>>): Enum = Enum(entries.map { it.name }.toTypedArray())
    }
}
