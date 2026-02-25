import com.zxhhyj.atorm.DoubaoLLMClient
import com.zxhhyj.atorm.LLMParams
import com.zxhhyj.atorm.LLModel
import com.zxhhyj.atorm.MusicSearchTool
import com.zxhhyj.atorm.StreamFrame
import com.zxhhyj.atorm.VideoSearchTool
import com.zxhhyj.atorm.dsl.prompt
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.time.measureTime

class DoubaoLLMClientTest {
    private val llmClient = DoubaoLLMClient(TODO("需要API KEY"))

    private val model = LLModel("doubao-seed-1-6-flash-250828", Long.MAX_VALUE)

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
            llmClient.executeStreaming(
                prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                    put("type", "disabled")
                }))) {
                    user("搜索泰勒斯威夫特的歌，搜索三体电视剧")
                },
                model = model,
                tools = listOf(MusicSearchTool, VideoSearchTool)
            ).filterIsInstance<StreamFrame.ToolCall>().toList()
        }
        println("执行时间: $measureTime")
    }
}