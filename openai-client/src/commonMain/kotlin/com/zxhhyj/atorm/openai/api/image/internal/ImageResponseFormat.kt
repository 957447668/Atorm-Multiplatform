package com.zxhhyj.atorm.openai.api.image.internal

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The format in which the generated images are returned.
 */
@JvmInline
@Serializable
public value class ImageResponseFormat(public val format: String) {

    public companion object {

        /**
         * Response format as url.
         */
        public val url: ImageResponseFormat = ImageResponseFormat("url")

        /**
         * Response format as base 64 json.
         */
        public val base64Json: ImageResponseFormat = ImageResponseFormat("b64_json")
    }
}
