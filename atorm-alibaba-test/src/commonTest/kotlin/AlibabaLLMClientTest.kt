import com.zxhhyj.atorm.AlibabaLLMClient
import com.zxhhyj.atorm.clients.LLMClient
import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.templates.LLMClientTest
import kotlin.test.Test

class AlibabaLLMClientTest : LLMClientTest() {

    override val llmClient: LLMClient = AlibabaLLMClient(TODO("需要API KEY"))

    override val model: LLModel = LLModel("qwen-flash", Long.MAX_VALUE)

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