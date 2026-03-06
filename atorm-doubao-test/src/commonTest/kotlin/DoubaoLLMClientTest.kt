import com.zxhhyj.atorm.DoubaoLLMClient
import com.zxhhyj.atorm.clients.LLMClient
import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.templates.LLMClientTest
import kotlin.test.Test

class DoubaoLLMClientTest : LLMClientTest() {
    override val llmClient: LLMClient = DoubaoLLMClient(TODO("需要API KEY"))

    override val model: LLModel = LLModel("doubao-seed-1-6-flash-250828", Long.MAX_VALUE)

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