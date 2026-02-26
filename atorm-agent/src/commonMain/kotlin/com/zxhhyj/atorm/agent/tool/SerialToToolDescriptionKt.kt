package com.zxhhyj.atorm.agent.tool

import com.zxhhyj.atorm.clients.LLMDescription
import com.zxhhyj.atorm.core.tool.ToolDescriptor
import com.zxhhyj.atorm.core.tool.ToolParameterDescriptor
import com.zxhhyj.atorm.core.tool.ToolParameterType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

private fun SerialDescriptor.description(): String =
    annotations.filterIsInstance<LLMDescription>().firstOrNull()?.description ?: ""

internal const val toolWrapperValueKey = "__wrapped_value__"

public fun SerialDescriptor.asToolDescriptor(
    toolName: String,
    toolDescription: String? = null,
    valueDescription: String? = null
): ToolDescriptor {
    val description = toolDescription ?: description()

    return when (kind) {
        PrimitiveKind.STRING -> ToolParameterType.String.asValueTool(toolName, description, valueDescription)
        PrimitiveKind.BOOLEAN -> ToolParameterType.Boolean.asValueTool(toolName, description, valueDescription)
        PrimitiveKind.CHAR -> ToolParameterType.String.asValueTool(toolName, description, valueDescription)
        PrimitiveKind.BYTE,
        PrimitiveKind.SHORT,
        PrimitiveKind.INT,
        PrimitiveKind.LONG -> ToolParameterType.Integer.asValueTool(toolName, description, valueDescription)

        PrimitiveKind.FLOAT,
        PrimitiveKind.DOUBLE -> ToolParameterType.Float.asValueTool(toolName, description, valueDescription)

        StructureKind.LIST -> ToolParameterType.List(
            getElementDescriptor(0).toToolParameterType()
        ).asValueTool(toolName, description, valueDescription)

        SerialKind.ENUM -> ToolParameterType.Enum(Array(elementsCount, ::getElementName))
            .asValueTool(toolName, description, valueDescription)

        StructureKind.CLASS -> {
            val required = mutableListOf<String>()
            val properties = parameterDescriptors(required)
            ToolDescriptor(
                toolName,
                description,
                requiredParameters = properties.filter { required.contains(it.name) },
                optionalParameters = properties.filterNot { required.contains(it.name) }
            )
        }

        PolymorphicKind.SEALED,
        StructureKind.OBJECT,
        SerialKind.CONTEXTUAL,
        PolymorphicKind.OPEN,
        StructureKind.MAP -> ToolDescriptor(
            name = toolName,
            description = description,
            requiredParameters = emptyList(),
            optionalParameters = emptyList()
        )
    }
}

public fun <T> KSerializer<T>.asToolDescriptorSerializer(): KSerializer<T> {
    val origSerializer = this

    return object : KSerializer<T> {
        override val descriptor: SerialDescriptor = origSerializer.descriptor

        override fun serialize(encoder: Encoder, value: T) {
            if (encoder !is JsonEncoder) throw IllegalStateException("Should be json encoder")

            val origSerialized = encoder.json.encodeToJsonElement(origSerializer, value)

            if (origSerialized is JsonObject) {
                require(toolWrapperValueKey !in origSerialized) {
                    "Serialized objects can't contain key '$toolWrapperValueKey', since this is a special key reserved to wrap primitive arguments in JSON objects"
                }

                encoder.encodeJsonElement(origSerialized)
            } else {
                encoder.encodeJsonElement(
                    buildJsonObject {
                        put(toolWrapperValueKey, origSerialized)
                    }
                )
            }
        }

        override fun deserialize(decoder: Decoder): T {
            if (decoder !is JsonDecoder) throw IllegalStateException("Should be json decoder")

            val deserialized = decoder
                .decodeJsonElement()
                .let {
                    require(it is JsonObject) {
                        "All serialized tool arguments must be represented as JSON objects, and primitives wrapped into a JSON object with key '$toolWrapperValueKey'"
                    }

                    it.jsonObject
                }

            return if (deserialized.keys == setOf(toolWrapperValueKey)) {
                decoder.json.decodeFromJsonElement(origSerializer, deserialized.getValue(toolWrapperValueKey))
            } else {
                decoder.json.decodeFromJsonElement(origSerializer, deserialized)
            }
        }
    }
}

private fun SerialDescriptor.toToolParameterType(): ToolParameterType = when (kind) {
    PrimitiveKind.CHAR,
    PrimitiveKind.STRING -> ToolParameterType.String

    PrimitiveKind.BOOLEAN -> ToolParameterType.Boolean
    PrimitiveKind.BYTE,
    PrimitiveKind.SHORT,
    PrimitiveKind.INT,
    PrimitiveKind.LONG -> ToolParameterType.Integer

    PrimitiveKind.FLOAT,
    PrimitiveKind.DOUBLE -> ToolParameterType.Float

    StructureKind.LIST -> ToolParameterType.List(getElementDescriptor(0).toToolParameterType())

    SerialKind.ENUM -> ToolParameterType.Enum(Array(elementsCount, ::getElementName))

    StructureKind.CLASS -> {
        val required = mutableListOf<String>()
        ToolParameterType.Object(
            parameterDescriptors(required),
            required,
            false
        )
    }

    PolymorphicKind.SEALED,
    StructureKind.OBJECT,
    SerialKind.CONTEXTUAL,
    PolymorphicKind.OPEN,
    StructureKind.MAP -> ToolParameterType.Object(
        emptyList(),
        emptyList(),
        true,
        ToolParameterType.String

    )
}

private fun ToolParameterType.asValueTool(name: String, description: String, valueDescription: String? = null) =
    ToolDescriptor(
        name = name,
        description = description,
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = toolWrapperValueKey,
                description = valueDescription ?: "",
                this
            )
        )
    )

private fun SerialDescriptor.parameterDescriptors(required: MutableList<String>): List<ToolParameterDescriptor> =
    List(elementsCount) { i ->
        val name = getElementName(i)
        val descriptor = getElementDescriptor(i)
        val isOptional = isElementOptional(i) || descriptor.isNullable

        if (!isOptional) {
            required.add(name)
        }

        ToolParameterDescriptor(
            name,
            getElementAnnotations(i).filterIsInstance<LLMDescription>().firstOrNull()?.description ?: name,
            getElementDescriptor(i).toToolParameterType()
        )
    }
