package com.zxhhyj.atorm.openai.client

import com.zxhhyj.atorm.openai.api.audio.SpeechRequest
import com.zxhhyj.atorm.openai.api.audio.Transcription
import com.zxhhyj.atorm.openai.api.audio.TranscriptionRequest
import com.zxhhyj.atorm.openai.api.audio.Translation
import com.zxhhyj.atorm.openai.api.audio.TranslationRequest
import com.zxhhyj.atorm.openai.api.core.RequestOptions

/**
 * Learn how to turn audio into text.
 */
public interface Audio {

    /**
     * Transcribes audio into the input language.
     *
     * @param request transcription request.
     * @param requestOptions request options.
     */
    public suspend fun transcription(
        request: TranscriptionRequest,
        requestOptions: RequestOptions? = null
    ): Transcription

    /**
     * Translates audio into English.
     *
     * @param request translation request.
     * @param requestOptions request options.
     */
    public suspend fun translation(request: TranslationRequest, requestOptions: RequestOptions? = null): Translation

    /**
     * Generates audio from the input text.
     *
     * @param request speech request.
     * @param requestOptions request options.
     */
    public suspend fun speech(request: SpeechRequest, requestOptions: RequestOptions? = null): ByteArray
}
