package com.zxhhyj.atorm.agent.tool

import com.zxhhyj.atorm.core.tool.ToolDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

public abstract class Tool<TArgs, TResult>(
    public val argsSerializer: KSerializer<TArgs>,
    public val resultSerializer: KSerializer<TResult>,
    public val descriptor: ToolDescriptor,
    public val metadata: Map<String, String> = emptyMap(),
) {
    public companion object {
        private val ToolJson: Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
            decodeEnumsCaseInsensitive = true
        }
    }

    public val name: String get() = descriptor.name

    private val actualArgsSerializer: KSerializer<TArgs> = argsSerializer

    protected open val json: Json = ToolJson

    public constructor(
        argsSerializer: KSerializer<TArgs>,
        resultSerializer: KSerializer<TResult>,
        name: String,
        description: String,
    ) : this(
        argsSerializer = argsSerializer,
        resultSerializer = resultSerializer,
        descriptor = argsSerializer.descriptor.asToolDescriptor(name, description)
    )

    public abstract suspend fun execute(args: TArgs): TResult

    public suspend fun executeUnsafe(args: Any?): TResult {
        return withUnsafeCast<TArgs, TResult>(
            args,
            "executeUnsafe argument must be castable to TArgs"
        ) { execute(it) }
    }

    public fun decodeArgs(rawArgs: JsonObject): TArgs = json.decodeFromJsonElement(actualArgsSerializer, rawArgs)

    public fun decodeResult(rawResult: JsonElement): TResult =
        json.decodeFromJsonElement(resultSerializer, rawResult)

    public fun encodeArgs(args: TArgs): JsonObject = json.encodeToJsonElement(actualArgsSerializer, args).jsonObject

    public fun encodeArgsUnsafe(args: Any?): JsonObject {
        return withUnsafeCast<TArgs, JsonObject>(
            args,
            "encodeArgsUnsafe argument must be castable to TArgs"
        ) { json.encodeToJsonElement(actualArgsSerializer, it).jsonObject }
    }

    public fun encodeResult(result: TResult): JsonElement =
        json.encodeToJsonElement(resultSerializer, result)

    public fun encodeResultUnsafe(result: Any?): JsonElement {
        return withUnsafeCast<TResult, JsonElement>(
            result,
            "encodeResultUnsafe argument must be castable to TResult",
        ) { encodeResult(it) }
    }

    public fun encodeArgsToString(args: TArgs): String = json.encodeToString(actualArgsSerializer, args)

    public fun encodeArgsToStringUnsafe(args: Any?): String {
        return withUnsafeCast<TArgs, String>(
            args,
            "encodeArgsToStringUnsafe argument must be castable to TArgs",
        ) { encodeArgsToString(it) }
    }

    public open fun encodeResultToString(result: TResult): String = json.encodeToString(resultSerializer, result)

    public fun encodeResultToStringUnsafe(result: Any?): String {
        return withUnsafeCast<TResult, String>(
            result,
            "encodeResultToStringUnsafe argument must be castable to TResult",
        ) { encodeResultToString(it) }
    }

    private inline fun <T, R> withUnsafeCast(
        input: Any?,
        errorMessage: String,
        action: (T) -> R,
    ): R {
        return try {
            @Suppress("UNCHECKED_CAST")
            action(input as T)
        } catch (e: ClassCastException) {
            throw ClassCastException(
                """
                Unsafe cast failed in tool with name: $name
                Error message: $errorMessage
                Original ClassCastException message: ${e.message}
                """.trimIndent()
            )
        }
    }

    @Deprecated("Extending Tool.Args is no longer required. Tool arguments are entirely handled by KotlinX Serialization.")
    @Suppress("DEPRECATION")
    public interface Args : ToolArgs

    @Deprecated("Extending Tool.Args is no longer required. Tool arguments are entirely handled by KotlinX Serialization.")
    @Suppress("DEPRECATION")
    @Serializable
    public data object EmptyArgs : Args
}
