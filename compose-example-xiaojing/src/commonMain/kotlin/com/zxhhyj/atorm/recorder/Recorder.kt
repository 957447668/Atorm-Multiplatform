package com.zxhhyj.atorm.recorder

import kotlinx.coroutines.flow.Flow
import kotlinx.io.Source

interface Recorder : Flow<Source> {
    val sampleRate: Int

    val sampleSizeInBits: Int

    val channels: Int

    val bufferSize: Int

    val isAvailable: Boolean

    fun startRecording()

    fun stopRecording()

    fun release()
}