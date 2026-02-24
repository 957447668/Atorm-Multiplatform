import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.aallam.openai.client.extension.mergeToChatMessage
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class Main {
    @Test
    fun test() = runBlocking {
        val config = OpenAIConfig(
            token = "nothing key",
            timeout = Timeout(socket = 60.seconds),
            host = OpenAIHost("https://ark.cn-beijing.volces.com/api/v3/")
        )

        val request = ChatCompletionRequest(
            model = ModelId("doubao-seed-1-6-flash-250828"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = "你叫什么名字!"
                )
            )
        )

        val openAI = OpenAI(config)

        openAI.chatCompletion(request) // 预热

        println("初始化完成")

        println("开始请求")
        val startTime = Clock.System.now().toEpochMilliseconds()
        var firstTokenReceived = false

        openAI.chatCompletions(request, buildJsonObject {
            put("thinking", buildJsonObject {
                put("type", "disabled")
            })
        }).collect {
            if (!firstTokenReceived) {
                val firstTokenTime = Clock.System.now().toEpochMilliseconds() - startTime
                println("首token时间: ${firstTokenTime}ms")
                firstTokenReceived = true
            }
            println(it.choices.mergeToChatMessage().content)
        }
    }
}