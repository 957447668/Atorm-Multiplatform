package com.zxhhyj.atorm.openai.api.assistant

import com.zxhhyj.atorm.openai.api.file.FileId
import com.zxhhyj.atorm.openai.api.vectorstore.VectorStoreId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ToolResources(
    @SerialName("file_search") public val fileSearch: FileSearchResources? = null,
    @SerialName("code_interpreter") public val codeInterpreter: CodeInterpreterResources? = null,
)

@Serializable
public data class FileSearchResources(
    @SerialName("vector_store_ids") val vectorStoreIds: List<VectorStoreId>? = null,
)

@Serializable
public data class CodeInterpreterResources(
    @SerialName("file_ids") val fileIds: List<FileId>? = null
)
