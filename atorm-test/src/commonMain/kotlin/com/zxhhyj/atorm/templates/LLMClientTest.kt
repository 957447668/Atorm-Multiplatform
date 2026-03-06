package com.zxhhyj.atorm.templates

import com.zxhhyj.atorm.clients.LLMClient
import com.zxhhyj.atorm.clients.executeStructured
import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.dsl.prompt
import com.zxhhyj.atorm.core.prompt.params.LLMParams
import com.zxhhyj.atorm.core.prompt.streaming.StreamFrame
import com.zxhhyj.atorm.schemas.StorySchema
import com.zxhhyj.atorm.tools.SearchMusicTool
import com.zxhhyj.atorm.tools.SearchVideoTool
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.measureTime

abstract class LLMClientTest {
    abstract val llmClient: LLMClient

    abstract val model: LLModel

    open fun chatTest() = runBlocking {
        llmClient.executeStreaming(
            prompt = prompt {
                user("你好！")
            },
            model = model
        ).collect()
    }

    open fun toolTest() = runBlocking {
        val measureTime = measureTime {
            val toolCalls = llmClient.executeStreaming(
                prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                    put("type", "disabled")
                }))) {
                    user("搜索泰勒斯威夫特的歌，搜索三体电视剧")
                },
                model = model,
                tools = listOf(SearchMusicTool.descriptor, SearchVideoTool.descriptor)
            ).filterIsInstance<StreamFrame.ToolCall>().toList()
            println(toolCalls)
        }
        println("执行时间: $measureTime")
    }

    open fun structuredTest() = runBlocking {
        val measureTime = measureTime {
            llmClient.executeStructured<StorySchema>(
                prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                    put("type", "disabled")
                }))) {
                    user("生成一个儿童故事，以 JSON 格式返回")
                },
                model = model
            )
        }
        println("执行时间: $measureTime")
    }
}