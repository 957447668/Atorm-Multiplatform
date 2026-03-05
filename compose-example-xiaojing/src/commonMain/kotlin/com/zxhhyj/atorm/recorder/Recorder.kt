package com.zxhhyj.atorm.recorder

import kotlinx.io.Source

interface Recorder {
    val isAvailable: Boolean

    suspend fun <T> buffer(length: Int, block: (Source) -> T): T

    fun startRecording()

    fun stopRecording()

    fun close()
}