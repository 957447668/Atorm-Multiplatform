package com.zxhhyj.atorm.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.Source

class AndroidAudio(
    override val sampleRate: Int,
    override val sampleSizeInBits: Int,
    override val channels: Int,
) : Audio {
    private val audioFlow = MutableSharedFlow<Source>()

    private val _audioState = MutableStateFlow<AudioState>(AudioState.NONE)

    override val audioState: Flow<AudioState> = _audioState

    override val isAvailable: Boolean = true

    private var audioJob: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override suspend fun emit(value: Source) {
        audioFlow.emit(value)
    }

    override fun play() {
        if (audioJob == null) {
            audioJob = coroutineScope.launch {
                val audioFormat = when (sampleSizeInBits) {
                    8 -> {
                        AudioFormat.ENCODING_PCM_8BIT
                    }

                    16 -> {
                        AudioFormat.ENCODING_PCM_16BIT
                    }

                    else -> throw NotImplementedError()
                }

                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    channels,
                    audioFormat
                )

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_UNKNOWN)
                            .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(audioFormat)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                val buffer = ByteArray(bufferSize)

                audioFlow.collect {
                    withContext(Dispatchers.IO) {
                        audioTrack.write(buffer, 0, buffer.size)
                    }
                }
            }
        }
        _audioState.value = AudioState.PLAYING
    }

    override fun pause() {
        _audioState.value = AudioState.PAUSED
    }

    override fun stop() {
        throw NotImplementedError()
    }

    override fun release() {
        coroutineScope.cancel()
    }
}