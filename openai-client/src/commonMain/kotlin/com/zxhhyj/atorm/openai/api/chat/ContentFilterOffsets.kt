package com.zxhhyj.atorm.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ContentFilterOffsets(
    @SerialName("check_offset") val checkOffset: Int?,
    @SerialName("start_offset") val startOffset: Int?,
    @SerialName("end_offset") val endOffset: Int?,
)
