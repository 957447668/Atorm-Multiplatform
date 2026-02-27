package com.zxhhyj.atorm.openai.client

import com.zxhhyj.atorm.openai.api.http.Timeout
import com.zxhhyj.atorm.openai.api.logging.LogLevel
import com.zxhhyj.atorm.openai.api.logging.Logger
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * OpenAI client configuration.
 *
 * @param token OpenAI Token
 * @param logging client logging configuration
 * @param timeout http client timeout
 * @param headers extra http headers
 * @param organization OpenAI organization ID
 * @param host OpenAI host configuration
 * @param proxy HTTP proxy url
 * @param host OpenAI host configuration.
 * @param retry rate limit retry configuration
 * @param engine explicit ktor engine for http requests.
 * @param httpClientConfig additional custom client configuration
 */
public class OpenAIConfig(
    public val token: String,
    public val logging: LoggingConfig = LoggingConfig(),
    public val timeout: Timeout = Timeout(socket = 30.seconds),
    public val organization: String? = null,
    public val headers: Map<String, String> = emptyMap(),
    public val host: OpenAIHost,
    public val proxy: ProxyConfig? = null,
    public val retry: RetryStrategy = RetryStrategy(),
    public val engine: HttpClientEngine? = null,
    public val httpClientConfig: HttpClientConfig<*>.() -> Unit = {}
)

public data class OpenAIHost(public val baseUrl: String, public val queryParams: Map<String, String> = emptyMap())

/** Proxy configuration. */
public sealed interface ProxyConfig {

    /** Creates an HTTP proxy from [url]. */
    public class Http(public val url: String) : ProxyConfig

    /** Create socks proxy from [host] and [port]. */
    public class Socks(public val host: String, public val port: Int) : ProxyConfig
}


/**
 * Specifies the retry strategy
 *
 * @param maxRetries the maximum amount of retries to perform for a request
 * @param base retry base value
 * @param maxDelay max retry delay
 */
public class RetryStrategy(
    public val maxRetries: Int = 3,
    public val base: Double = 2.0,
    public val maxDelay: Duration = 60.seconds,
)

/**
 * Defines the configuration parameters for logging.
 *
 * @property logLevel the level of logging to be used by the HTTP client.
 * @property logger the logger instance to be used by the HTTP client.
 * @property sanitize flag indicating whether to sanitize sensitive information (i.e., authorization header) in the logs
 */
public class LoggingConfig(
    public val logLevel: LogLevel = LogLevel.Headers,
    public val logger: Logger = Logger.Simple,
    public val sanitize: Boolean = true,
)
