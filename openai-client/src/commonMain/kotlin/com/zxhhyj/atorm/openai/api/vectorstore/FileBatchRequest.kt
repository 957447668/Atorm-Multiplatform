package com.zxhhyj.atorm.openai.api.vectorstore

import com.zxhhyj.atorm.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A batch file request.
 */
@Serializable
public data class FileBatchRequest(

    /**
     * A list of File IDs that the vector store should use. Useful for tools like file_search that can access files.
     */
    @SerialName("file_ids") public val fileIds: List<FileId>,
)
