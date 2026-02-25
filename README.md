# Atorm Multiplatform

一个用 Kotlin 编写的跨平台 LLM (Large Language Model) 集成框架，支持 Android、iOS、MacOS (Native) 和 JVM 平台。

## 核心特性

### 跨平台支持

- **多平台兼容**: 统一的 Kotlin API 支持 Android、iOS、MacOS (Native)、JVM
- **零配置部署**: 简单集成，无需复杂配置
- **一致行为**: 所有平台上提供相同的 LLM 集成体验

### LLM 集成

- **Doubao 集成**: 支持字节跳动的 Doubao LLM
- **OpenAI 集成**: 支持 OpenAI 的 GPT 系列模型
- **统一接口**: 提供统一的 LLM 客户端接口，简化不同 LLM 的切换

### 开发体验

- **类型安全**: Kotlin DSL 提供编译时类型检查
- **协程支持**: 充分利用 Kotlin 协程的异步能力
- **流式响应**: 支持 LLM 的流式响应，提供实时反馈
- **工具调用**: 支持 LLM 工具调用功能，扩展 LLM 能力

### 性能优化

- **高效网络**: 优化的网络请求处理
- **内存管理**: 合理的内存使用，避免内存泄漏
- **错误处理**: 完善的错误处理机制

## 快速开始

### 1. 添加依赖

在你的 `build.gradle.kts` 文件中添加依赖：

```kotlin
dependencies {
    // 核心库
    implementation(project(":atorm-core"))
    
    // 选择需要的 LLM 集成
    implementation(project(":atorm-doubao")) // Doubao LLM 集成
    // 或
    implementation(project(":atorm-openai")) // OpenAI 集成
}
```

### 2. 初始化 LLM 客户端

```kotlin
// 初始化 Doubao LLM 客户端
val doubaoClient = DoubaoLLMClient(apiKey = "your-doubao-api-key")

// 或初始化 OpenAI 客户端
val openaiClient = OpenAIClient(apiKey = "your-openai-api-key")
```

### 3. 发送请求

#### 基本聊天

```kotlin
val prompt = prompt {
    user("你好！")
}

val model = LLModel("doubao-seed-1-6-flash-250828", 0)

// 流式响应
client.executeStreaming(prompt, model, emptyList()).collect {
    when (it) {
        is StreamFrame.Append -> println("Received: ${it.text}")
        is StreamFrame.ToolCall -> println("Tool call: ${it.name}")
        is StreamFrame.End -> println("Completed")
    }
}
```

#### 工具调用

```kotlin
val prompt = prompt {
    user("我想听泰勒斯威夫特的歌")
}

val model = LLModel("doubao-seed-1-6-flash-250828", 0)

// 流式响应，包含工具调用
client.executeStreaming(prompt, model, listOf(MusicSearchTool)).collect {
    when (it) {
        is StreamFrame.Append -> println("Received: ${it.text}")
        is StreamFrame.ToolCall -> {
            println("Tool call: ${it.name}")
            println("Arguments: ${it.content}")
            // 处理工具调用
        }
        is StreamFrame.End -> println("Completed")
    }
}
```

## 项目结构

```
atorm/
├── atorm-core/            # 核心库，包含基础接口和模型
├── atorm-core-test/       # 核心库测试
├── atorm-doubao/          # Doubao LLM 集成
├── atorm-doubao-test/     # Doubao LLM 集成测试
├── atorm-openai/          # OpenAI 集成
├── atorm-openai-test/     # OpenAI 集成测试
├── composeApp/            # 示例 Compose 应用
└── README.md              # 项目文档
```

### 核心模块

- **atorm-core**: 定义了 LLM 集成的核心接口和模型，包括 `LLMClient`、`Prompt`、`StreamFrame` 等
- **atorm-doubao**: 实现了与字节跳动 Doubao LLM 的集成
- **atorm-openai**: 实现了与 OpenAI 的集成

## 支持的 LLM

| LLM 提供商 | 模块 | 支持的功能 |
|----------|------|-----------|
| Doubao | atorm-doubao | 聊天、流式响应、工具调用 |
| OpenAI | atorm-openai | 聊天、流式响应、工具调用 |

## 示例应用

项目包含一个 Compose 示例应用，位于 `composeApp` 目录，展示了如何使用 Atorm 与 LLM 进行交互。

## 许可证

本项目采用 Apache 许可证。详见 [LICENSE](./LICENSE) 文件。