package com.zxhhyj.atorm.recorder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import kotlinx.io.Source
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

class JvmPlatformRecorder(
    override val sampleRate: Int,
    override val sampleSizeInBits: Int,
    override val channels: Int,
    override val bufferSize: Int,
) : Recorder {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val recordJob = Job()

    private val audioFormat: AudioFormat = AudioFormat(sampleRate.toFloat(), sampleSizeInBits, channels, true, false)
    private val audioLine = AudioSystem.getTargetDataLine(audioFormat)

    private val bufferFlow = MutableSharedFlow<Buffer>()

    override val isAvailable: Boolean
        get() = true

    override suspend fun collect(collector: FlowCollector<Source>) {
        bufferFlow.collect(collector)
    }

    override fun startRecording() {
        coroutineScope.launch(recordJob) {
            val bytes = ByteArray(bufferSize)
            val buffer = Buffer()
            while (isActive) {
                val read = withContext(Dispatchers.IO) {
                    audioLine.read(bytes, 0, bytes.size)
                }
                if (read > 0) {
                    bufferFlow.emit(buffer.apply {
                        write(bytes)
                    })
                    buffer.clear()
                }
            }
        }
    }

    override fun stopRecording() {
        recordJob.cancelChildren()
    }

    override fun close() {
        coroutineScope.cancel()
        audioLine.close()
    }
}