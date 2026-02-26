package com.zxhhyj.atorm.core.tool

public open class ToolDescriptor(
    public val name: String,
    public val description: String,
    public val requiredParameters: List<ToolParameterDescriptor> = emptyList(),
    public val optionalParameters: List<ToolParameterDescriptor> = emptyList(),
) {
    public fun copy(
        name: String = this.name,
        description: String = this.description,
        requiredParameters: List<ToolParameterDescriptor> = this.requiredParameters.toList(),
        optionalParameters: List<ToolParameterDescriptor> = this.optionalParameters.toList(),
    ): ToolDescriptor {
        return ToolDescriptor(
            name = name,
            description = description,
            requiredParameters = requiredParameters,
            optionalParameters = optionalParameters,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ToolDescriptor) return false

        if (name != other.name) return false
        if (description != other.description) return false
        if (requiredParameters != other.requiredParameters) return false
        if (optionalParameters != other.optionalParameters) return false

        return true
    }

    override fun toString(): String {
        return "ToolDescriptor(name=$name, description=$description, requiredParameters=$requiredParameters, optionalParameters=$optionalParameters)"
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + requiredParameters.hashCode()
        result = 31 * result + optionalParameters.hashCode()
        return result
    }
}
