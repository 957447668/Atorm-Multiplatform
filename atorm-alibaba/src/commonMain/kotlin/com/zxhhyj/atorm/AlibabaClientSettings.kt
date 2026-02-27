package com.zxhhyj.atorm

import com.zxhhyj.atorm.openai.api.http.Timeout
import kotlin.time.Duration.Companion.seconds

public data class AlibabaClientSettings(
    val baseUrl: String = "https://dashscope.aliyuncs.com/compatible-mode/v1/",
    val timeout: Timeout = Timeout(socket = 60.seconds)
)