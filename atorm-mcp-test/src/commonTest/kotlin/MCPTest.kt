import com.zxhhyj.atorm.agent.mcp.McpToolRegistryProvider
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test

class MCPTest {

    @Test
    fun test() {
        runBlocking {
            val toolSet =
                McpToolRegistryProvider.formStreamableHttp(url = "https://mcp.api-inference.modelscope.net/148b182d489041/mcp")

            val tool = toolSet.getTool("bing_search")
            val result = tool.executeUnsafe(buildJsonObject {
                put("query", "双笙")
                put("count", 10)
                put("offset", 0)
            })

            println(result)
        }
    }
}