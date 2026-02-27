import com.zxhhyj.atorm.clients.LLMDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("StorySchema")
data class StorySchema(
    @property:LLMDescription("故事的内容")
    val story: String,

    @property:LLMDescription("故事标题")
    val title: String,
)