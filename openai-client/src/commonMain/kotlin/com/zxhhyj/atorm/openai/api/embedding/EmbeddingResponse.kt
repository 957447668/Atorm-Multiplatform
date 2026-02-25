package com.zxhhyj.atorm.openai.api.embedding

import com.zxhhyj.atorm.openai.api.core.Usage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create embeddings response.
 */
@Serializable
public class EmbeddingResponse(

    /**
     * An embedding results.
     */
    @SerialName("data") public val embeddings: List<Embedding>,

    /**
     * Embedding usage data.
     */
    @SerialName("usage") public val usage: Usage,
)
