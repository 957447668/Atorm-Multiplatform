package com.zxhhyj.atorm.openai.client.internal

import com.zxhhyj.atorm.openai.client.Assistants
import com.zxhhyj.atorm.openai.client.Audio
import com.zxhhyj.atorm.openai.client.Batch
import com.zxhhyj.atorm.openai.client.Chat
import com.zxhhyj.atorm.openai.client.Completions
import com.zxhhyj.atorm.openai.client.Edits
import com.zxhhyj.atorm.openai.client.Embeddings
import com.zxhhyj.atorm.openai.client.Files
import com.zxhhyj.atorm.openai.client.FineTunes
import com.zxhhyj.atorm.openai.client.FineTuning
import com.zxhhyj.atorm.openai.client.Images
import com.zxhhyj.atorm.openai.client.Messages
import com.zxhhyj.atorm.openai.client.Models
import com.zxhhyj.atorm.openai.client.Moderations
import com.zxhhyj.atorm.openai.client.OpenAI
import com.zxhhyj.atorm.openai.client.Responses
import com.zxhhyj.atorm.openai.client.Runs
import com.zxhhyj.atorm.openai.client.Threads
import com.zxhhyj.atorm.openai.client.VectorStores
import com.zxhhyj.atorm.openai.client.internal.api.AssistantsApi
import com.zxhhyj.atorm.openai.client.internal.api.AudioApi
import com.zxhhyj.atorm.openai.client.internal.api.BatchApi
import com.zxhhyj.atorm.openai.client.internal.api.ChatApi
import com.zxhhyj.atorm.openai.client.internal.api.CompletionsApi
import com.zxhhyj.atorm.openai.client.internal.api.EditsApi
import com.zxhhyj.atorm.openai.client.internal.api.EmbeddingsApi
import com.zxhhyj.atorm.openai.client.internal.api.FilesApi
import com.zxhhyj.atorm.openai.client.internal.api.FineTunesApi
import com.zxhhyj.atorm.openai.client.internal.api.FineTuningApi
import com.zxhhyj.atorm.openai.client.internal.api.ImagesApi
import com.zxhhyj.atorm.openai.client.internal.api.MessagesApi
import com.zxhhyj.atorm.openai.client.internal.api.ModelsApi
import com.zxhhyj.atorm.openai.client.internal.api.ModerationsApi
import com.zxhhyj.atorm.openai.client.internal.api.ResponsesApi
import com.zxhhyj.atorm.openai.client.internal.api.RunsApi
import com.zxhhyj.atorm.openai.client.internal.api.ThreadsApi
import com.zxhhyj.atorm.openai.client.internal.api.VectorStoresApi
import com.zxhhyj.atorm.openai.client.internal.http.HttpRequester

/**
 * Implementation of [OpenAI].
 *
 * @param requester http transport layer
 */
internal class OpenAIApi(
    private val requester: HttpRequester
) : OpenAI,
    Completions by CompletionsApi(requester),
    Files by FilesApi(requester),
    Edits by EditsApi(requester),
    Embeddings by EmbeddingsApi(requester),
    Models by ModelsApi(requester),
    Moderations by ModerationsApi(requester),
    FineTunes by FineTunesApi(requester),
    Images by ImagesApi(requester),
    Chat by ChatApi(requester),
    Audio by AudioApi(requester),
    FineTuning by FineTuningApi(requester),
    Assistants by AssistantsApi(requester),
    Threads by ThreadsApi(requester),
    Runs by RunsApi(requester),
    Messages by MessagesApi(requester),
    VectorStores by VectorStoresApi(requester),
    Batch by BatchApi(requester),
    Responses by ResponsesApi(requester),
    AutoCloseable by requester
