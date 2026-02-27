package com.zxhhyj.atorm

import com.zxhhyj.atorm.openai.api.http.Timeout
import kotlin.time.Duration.Companion.seconds

public data class DoubaoClientSettings(
    val baseUrl: String = "https://ark.cn-beijing.volces.com/api/v3/",
    val timeout: Timeout = Timeout(socket = 60.seconds)
)