package com.zxhhyj.atorm.recorder

import kotlinx.io.Source

interface Recorder {
    val isAvailable: Boolean

    suspend fun <T> buffer(block: (Source) -> T): T

    fun startRecording()

    fun stopRecording()

    fun close()
}