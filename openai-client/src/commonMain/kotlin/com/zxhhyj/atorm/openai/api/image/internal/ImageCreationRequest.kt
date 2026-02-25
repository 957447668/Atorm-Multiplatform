package com.zxhhyj.atorm.openai.api.image.internal

import com.zxhhyj.atorm.openai.api.image.ImageSize
import com.zxhhyj.atorm.openai.api.image.Quality
import com.zxhhyj.atorm.openai.api.image.Style
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Image generation request.
 * Results are expected as URLs.
 */
@Serializable
public data class ImageCreationRequest(
    @SerialName("prompt") val prompt: String,
    @SerialName("n") val n: Int? = null,
    @SerialName("size") val size: ImageSize? = null,
    @SerialName("user") val user: String? = null,
    @SerialName("response_format") val responseFormat: ImageResponseFormat,
    @SerialName("model") val model: String? = null,
    @SerialName("quality") val quality: Quality? = null,
    @SerialName("style") val style: Style? = null,
)
