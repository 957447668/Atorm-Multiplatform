import com.zxhhyj.atorm.AlibabaLLMClient
import com.zxhhyj.atorm.MusicSearchTool
import com.zxhhyj.atorm.VideoSearchTool
import com.zxhhyj.atorm.clients.executeStructured
import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.dsl.prompt
import com.zxhhyj.atorm.core.prompt.params.LLMParams
import com.zxhhyj.atorm.core.prompt.streaming.StreamFrame
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.time.measureTime

class AlibabaLLMClientTest {
    private val llmClient = AlibabaLLMClient(TODO("需要API KEY"))

    private val model = LLModel("qwen-flash", Long.MAX_VALUE)

    init {
        runBlocking {
            llmClient.execute(
                prompt = prompt {
                    user("你好！")
                },
                model = model
            )
        }
    }

    @Test
    fun chatTest() = runBlocking {
        llmClient.executeStreaming(
            prompt = prompt {
                user("你好！")
            },
            model = model
        ).collect()
    }

    @Test
    fun toolTest() = runBlocking {
        val measureTime = measureTime {
            val toolCalls = llmClient.executeStreaming(
                prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                    put("type", "disabled")
                }))) {
                    user("搜索泰勒斯威夫特的歌，搜索三体电视剧")
                },
                model = model,
                tools = listOf(MusicSearchTool, VideoSearchTool)
            ).filterIsInstance<StreamFrame.ToolCall>().toList()
            println(toolCalls)
        }
        println("执行时间: $measureTime")
    }

    @Test
    fun structuredTest() = runBlocking {
        val measureTime = measureTime {
            val storySchema = llmClient.executeStructured<StorySchema>(
                prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                    put("type", "disabled")
                }))) {
                    user("给我讲一个儿童故事")
                },
                model = model
            )
        }
        println("执行时间: $measureTime")
    }
}