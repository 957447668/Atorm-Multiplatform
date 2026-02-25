package com.zxhhyj.atorm.openai.client

import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.api.file.File
import com.zxhhyj.atorm.openai.api.file.FileId
import com.zxhhyj.atorm.openai.api.file.FileUpload

/**
 * Files are used to upload documents that can be used across features like [Answers], [Searches], and [Classifications]
 */
public interface Files {

    /**
     * Upload a file that contains document(s) to be used across various endpoints/features.
     * Currently, the size of all the files uploaded by one organization can be up to 1 GB.
     *
     * @param request file upload request.
     * @param requestOptions request options.
     */
    public suspend fun file(request: FileUpload, requestOptions: RequestOptions? = null): File

    /**
     * Returns a list of files that belong to the user's organization.
     *
     * @param requestOptions request options.
     */
    public suspend fun files(requestOptions: RequestOptions? = null): List<File>

    /**
     * Returns information about a specific file.
     *
     * @param fileId the ID of the file to retrieve.
     * @param requestOptions request options.
     */
    public suspend fun file(fileId: FileId, requestOptions: RequestOptions? = null): File?

    /**
     * Delete a file. Only owners of organizations can delete files currently.
     *
     * @param fileId the ID of the file to delete.
     * @param requestOptions request options.
     */
    public suspend fun delete(fileId: FileId, requestOptions: RequestOptions? = null): Boolean

    /**
     * Returns the contents of the specified [fileId].
     *
     * @param fileId the ID of the file to download.
     * @param requestOptions request options.
     */
    public suspend fun download(fileId: FileId, requestOptions: RequestOptions? = null): ByteArray
}
