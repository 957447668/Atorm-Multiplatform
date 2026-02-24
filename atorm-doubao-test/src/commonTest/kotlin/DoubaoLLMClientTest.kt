import com.zxhhyj.atorm.DoubaoLLMClient
import com.zxhhyj.atorm.LLMParams
import com.zxhhyj.atorm.LLModel
import com.zxhhyj.atorm.MusicSearchTool
import com.zxhhyj.atorm.dsl.prompt
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test

class DoubaoLLMClientTest {
    @Test
    fun chatTest() = runBlocking {
        val llmClient = DoubaoLLMClient(TODO())
        llmClient.executeStreaming(
            prompt = prompt {
                user("你好！")
            },
            model = LLModel("doubao-seed-1-6-flash-250828", 0)
        ).collect()
    }

    @Test
    fun toolTest() = runBlocking {
        val llmClient = DoubaoLLMClient(TODO())
        val toList = llmClient.executeStreaming(
            prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                put("type", "disabled")
            }))) {
                user("我想听泰勒斯威夫特的歌，然后还想听双笙的歌")
            },
            model = LLModel("doubao-seed-1-6-flash-250828", 0),
            tools = listOf(MusicSearchTool)
        ).toList()
        toList
    }
}