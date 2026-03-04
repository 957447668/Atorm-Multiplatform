package com.zxhhyj.atorm.agent.mcp

import com.zxhhyj.atorm.agent.tool.ToolSet
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.request.HttpRequestBuilder
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.StreamableHttpClientTransport
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import kotlin.time.Duration

public object McpToolRegistryProvider {

    public const val DEFAULT_MCP_CLIENT_NAME: String = "mcp-client-cli"

    public const val DEFAULT_MCP_CLIENT_VERSION: String = "1.0.0"

    public fun streamableHttpClientTransport(
        client: HttpClient = HttpClient(),
        url: String,
        reconnectionTime: Duration? = null,
        requestBuilder: HttpRequestBuilder.() -> Unit = {},
    ): StreamableHttpClientTransport = StreamableHttpClientTransport(
        client = client.config {
            install(SSE)
        },
        url = url,
        reconnectionTime = reconnectionTime,
        requestBuilder = requestBuilder
    )

    public suspend fun formStreamableHttp(
        client: HttpClient = HttpClient(),
        url: String,
        reconnectionTime: Duration? = null,
        requestBuilder: HttpRequestBuilder.() -> Unit = {},
    ): ToolSet {
        val transport = streamableHttpClientTransport(client, url, reconnectionTime, requestBuilder)

        val client = Client(
            clientInfo = Implementation(
                name = DEFAULT_MCP_CLIENT_NAME,
                version = DEFAULT_MCP_CLIENT_VERSION
            )
        )

        client.connect(transport)

        return fromClient(client)
    }

    public suspend fun fromClient(
        mcpClient: Client,
        mcpToolParser: McpToolDescriptorParser = DefaultMcpToolDescriptorParser,
    ): ToolSet {
        val sdkTools = mcpClient.listTools().tools
        return ToolSet {
            sdkTools.forEach { sdkTool ->
                val toolDescriptor = mcpToolParser.parse(sdkTool)
                tool(McpTool(mcpClient, toolDescriptor))
            }
        }
    }
}
