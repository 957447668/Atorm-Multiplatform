package com.zxhhyj.atorm.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.io.Source

interface Audio : FlowCollector<Source> {
    val sampleRate: Int

    val sampleSizeInBits: Int

    val channels: Int

    val audioState: Flow<AudioState>

    val isAvailable: Boolean

    fun play()

    fun pause()

    fun stop()

    fun release()
}