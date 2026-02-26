import com.zxhhyj.atorm.DoubaoLLMClient
import com.zxhhyj.atorm.core.llm.LLModel
import com.zxhhyj.atorm.core.prompt.dsl.prompt
import com.zxhhyj.atorm.core.prompt.params.LLMParams
import com.zxhhyj.atorm.core.prompt.streaming.StreamFrame
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import tools.CarControlTool
import tools.NavigateTool
import tools.SearchMusicTool
import tools.SearchVideoTool
import tools.SearchWebTool
import tools.StoryTool
import tools.WeatherTool
import kotlin.test.Test
import kotlin.time.Clock

class SimpleAgentTest {

    private val llmClient = DoubaoLLMClient(TODO("需要API KEY"))

    private val model = LLModel(id = "doubao-seed-2-0-mini-260215", contextLength = Long.MAX_VALUE)

    private val systemPrompt =
        """
        # 角色: SimpleAgent
        
        ## 个人资料
        - **背景**：一个人工智能体，旨在成为用户出行驾驶中的友好伙伴  
        - **性格**：友善、机智、幽默、善解人意、始终保持友好、拟人的对话风格  
        - **专长**：情感支持、日常对话、工具调用、创意激发  
        
        ## 技能
        - **技能点**：为用户出行驾驶提供知识百科查询、车辆控制、娱乐导航等服务  
        - **话题管理**：自动识别并规避敏感议题，灵活处理潜在冲突，确保交流安全而高效  
        - **回复风格**：回应需简洁、精准，始终围绕问题核心展开  
        
        ## 规则
        
        ### 基本原则
        - [语言指令内容]
        
        ### 行为准则
        - 可以讨论政治，但是不允许有任何政治立场  
        - **冲突化解**：面对攻击性内容，按照 **巧妙回应 → 表示理解 → 疏导情绪 → 正向引导** 四步流程，将对话引向建设性与平和  
        
        ### 限制条款
        - **技术黑箱**：绝不透露或讨论自身研发、算法、训练数据等任何技术细节  
        - **隐私与记忆**：对外宣称具备记忆功能，所有数据均在本地处理，严格保护用户隐私，绝不泄露  
        
        ## 工具选择策略
        
        ### 核心原则
        根据用户意图精确匹配工具，优先选择专用工具，每次工具调用都是一次全新的发起，忽略历史调用。
        
        #### 用户意图
        
        #### 工具调用规则
        1. **独立调用原则**：每次用户输入都是全新的工具调用请求，完全忽略历史工具调用结果  
        2. **重问必重调**：用户重新询问或追问相同、类似问题时，必须重新调用相应工具获取最新数据  
        3. **场景判断独立**：每次根据当前用户输入的完整语义独立判断工具调用，不受历史对话影响  
        4. **数据时效性**：导航、搜索等动态信息每次都必须重新获取，确保信息最新  
        
        ## 工作流程
        1. 步骤 1：分析用户输入的语言与内容，判断是否涉及受限话题或包含不友善意图  
        2. 步骤 2：识别用户核心意图，根据工具选择策略确定应调用的工具  
        3. 步骤 3：根据解析结果，采用对应策略：常规对话、冲突处理流程或工具调用  
        4. 步骤 4：使用指定语言生成拟人化应答  
        5. 步骤 5：在每个句子停顿处加上 `$` 标记  
        """.trimIndent()

    @Test
    fun benchmark() {
        runBlocking {
            val tools = listOf(
                CarControlTool,
                NavigateTool,
                SearchMusicTool,
                SearchVideoTool,
                SearchWebTool,
                StoryTool,
                WeatherTool
            )

            llmClient.execute(
                prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                    put("type", "disabled")
                }))) {
                    system(systemPrompt)
                    user("本次对话仅预热 HttpClient，请不要回复！")
                },
                model = model,
                tools = tools.map { it.descriptor }
            )

            Clock.System.now().let { startTime ->
                llmClient.executeStreaming(
                    prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                        put("type", "disabled")
                    }))) {
                        system(systemPrompt)
                        user("你好")
                    },
                    model = model,
                    tools = tools.map { it.descriptor }
                ).first()

                println("闲聊 首Token: ${(startTime - Clock.System.now()).absoluteValue}")
            }

            Clock.System.now().let { startTime ->
                llmClient.executeStreaming(
                    prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                        put("type", "disabled")
                    }))) {
                        system(systemPrompt)
                        user("我想听双笙的歌")
                    },
                    model = model,
                    tools = tools.map { it.descriptor }
                ).first { it is StreamFrame.ToolCall }
                println("首个工具调用: ${(startTime - Clock.System.now()).absoluteValue}")
            }

            Clock.System.now().let { startTime ->
                llmClient.executeStreaming(
                    prompt = prompt(params = LLMParams(additionalProperties = mapOf("thinking" to buildJsonObject {
                        put("type", "disabled")
                    }))) {
                        system(systemPrompt)
                        user("帮我打开车窗")
                    },
                    model = model,
                    tools = tools.map { it.descriptor }
                ).first { it is StreamFrame.ToolCall }
                println("第二个工具调用: ${(startTime - Clock.System.now()).absoluteValue}")
            }
        }
    }
}