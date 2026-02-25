package com.zxhhyj.atorm.openai.client.internal.api

import com.zxhhyj.atorm.openai.api.core.ListResponse
import com.zxhhyj.atorm.openai.api.core.RequestOptions
import com.zxhhyj.atorm.openai.api.image.ImageCreation
import com.zxhhyj.atorm.openai.api.image.ImageEdit
import com.zxhhyj.atorm.openai.api.image.ImageJSON
import com.zxhhyj.atorm.openai.api.image.ImageURL
import com.zxhhyj.atorm.openai.api.image.ImageVariation
import com.zxhhyj.atorm.openai.api.image.internal.ImageCreationRequest
import com.zxhhyj.atorm.openai.api.image.internal.ImageResponseFormat
import com.zxhhyj.atorm.openai.api.model.ModelId
import com.zxhhyj.atorm.openai.client.Images
import com.zxhhyj.atorm.openai.client.internal.extension.appendFileSource
import com.zxhhyj.atorm.openai.client.internal.extension.requestOptions
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester
import com.zxhhyj.atorm.openai.client.internal.http.perform
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class ImagesApi(private val requester: HttpRequester) : Images {
    private val defaultEditModel = ModelId("dall-e-2")

    override suspend fun imageURL(creation: ImageCreation, requestOptions: RequestOptions?): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.post {
                url(path = ApiPath.ImagesGeneration)
                setBody(creation.toURLRequest())
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun imageJSON(creation: ImageCreation, requestOptions: RequestOptions?): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.post {
                url(path = ApiPath.ImagesGeneration)
                setBody(creation.toJSONRequest())
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun imageURL(edit: ImageEdit, requestOptions: RequestOptions?): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.submitFormWithBinaryData(
                url = ApiPath.ImagesEdits,
                formData = imageEditRequest(edit, ImageResponseFormat.url),
            ) {
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun imageJSON(edit: ImageEdit, requestOptions: RequestOptions?): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.submitFormWithBinaryData(
                url = ApiPath.ImagesEdits,
                formData = imageEditRequest(edit, ImageResponseFormat.base64Json),
            ) {
                requestOptions(requestOptions)
            }
        }.data
    }

    /**
     * Build image edit request.
     */
    private fun imageEditRequest(edit: ImageEdit, responseFormat: ImageResponseFormat) = formData {
        appendFileSource("image", edit.image, ContentType.Image.PNG)
        appendFileSource("mask", edit.mask, ContentType.Image.PNG)
        append(key = "prompt", value = edit.prompt)
        append(key = "response_format", value = responseFormat.format)
        edit.n?.let { n -> append(key = "n", value = n) }
        edit.size?.let { dim -> append(key = "size", value = dim.size) }
        edit.user?.let { user -> append(key = "user", value = user) }
        append(key = "model", value = (edit.model ?: defaultEditModel).id)
    }

    override suspend fun imageURL(variation: ImageVariation, requestOptions: RequestOptions?): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.submitFormWithBinaryData(
                url = ApiPath.ImagesVariants,
                formData = imageVariantRequest(variation, ImageResponseFormat.url),
            ) {
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun imageJSON(variation: ImageVariation, requestOptions: RequestOptions?): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.submitFormWithBinaryData(
                url = ApiPath.ImagesVariants,
                formData = imageVariantRequest(variation, ImageResponseFormat.base64Json),
            ) {
                requestOptions(requestOptions)
            }
        }.data
    }

    /**
     * Build image variant request.
     */
    private fun imageVariantRequest(edit: ImageVariation, responseFormat: ImageResponseFormat) = formData {
        appendFileSource("image", edit.image, ContentType.Image.PNG)
        append(key = "response_format", value = responseFormat.format)
        edit.n?.let { n -> append(key = "n", value = n) }
        edit.size?.let { dim -> append(key = "size", value = dim.size) }
        edit.user?.let { user -> append(key = "user", value = user) }
        edit.model?.let { model -> append(key = "model", value = model.id) }
    }

    /** Convert [com.zxhhyj.atorm.openai.api.image.ImageCreation] instance to base64 JSON request */
    private fun ImageCreation.toJSONRequest() = ImageCreationRequest(
        prompt = prompt,
        n = n,
        size = size,
        user = user,
        responseFormat = ImageResponseFormat.base64Json,
        model = model?.id,
        quality = quality,
        style = style,
    )

    /** Convert [com.zxhhyj.atorm.openai.api.image.ImageCreation] instance to URL request */
    private fun ImageCreation.toURLRequest() = ImageCreationRequest(
        prompt = prompt,
        n = n,
        size = size,
        user = user,
        responseFormat = ImageResponseFormat.url,
        model = model?.id,
        quality = quality,
        style = style,
    )
}
