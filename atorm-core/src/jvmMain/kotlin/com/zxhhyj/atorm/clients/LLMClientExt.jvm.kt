package com.zxhhyj.atorm.clients

import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.Prompt
import com.zxhhyj.atorm.core.prompt.message.Message
import com.zxhhyj.atorm.core.prompt.params.LLMParams
import kotlinx.schema.generator.json.serialization.SerializationClassJsonSchemaGenerator
import kotlinx.schema.generator.json.serialization.SerializationClassSchemaIntrospector
import kotlinx.schema.json.encodeToJsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@PublishedApi
internal val generator: SerializationClassJsonSchemaGenerator = SerializationClassJsonSchemaGenerator(
    introspectorConfig = SerializationClassSchemaIntrospector.Config(
        descriptionExtractor = { annotations ->
            annotations.filterIsInstance<LLMDescription>().firstOrNull()?.description
        },
    ),
)

@PublishedApi
internal val json: Json = Json { prettyPrint = true }

public actual suspend inline fun <reified T> LLMClient.executeStructured(
    prompt: Prompt,
    model: LLModel,
    examples: List<T>
): T {
    val serializer = serializer<T>()

    val jsonSchema = generator.generateSchema(serializer.descriptor)
    val jsonObject = jsonSchema.encodeToJsonObject()

    val responses = execute(
        prompt = prompt.withUpdatedParams {
            schema = LLMParams.Schema(serializer.descriptor.serialName, jsonObject)
        },
        model = model
    ).filterNot { it is Message.Reasoning }.single()

    return json.decodeFromString<T>(responses.content)
}