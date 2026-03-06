import com.zxhhyj.atorm.OpenAILLMClient
import com.zxhhyj.atorm.clients.LLMClient
import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.templates.LLMClientTest
import kotlin.test.Test

class OpenAILLMClientTest : LLMClientTest() {
    override val llmClient: LLMClient = OpenAILLMClient(TODO("需要API KEY"))

    override val model: LLModel = LLModel("chatgpt-4o", Long.MAX_VALUE)

    @Test
    override fun chatTest() {
        super.chatTest()
    }

    @Test
    override fun toolTest() {
        super.toolTest()
    }

    @Test
    override fun structuredTest() {
        super.structuredTest()
    }
}