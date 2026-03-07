package com.zxhhyj.atorm.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.io.Source

class JvmAudio(
    override val sampleRate: Int,
    override val sampleSizeInBits: Int,
    override val channels: Int,
) : Audio {
    override val audioState: Flow<AudioState> = emptyFlow()
    override val isAvailable: Boolean = true

    override suspend fun emit(value: Source) {
        TODO("Not yet implemented")
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}