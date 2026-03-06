package com.zxhhyj.atorm.recorder

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
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
            try {
                audioLine.open(audioFormat, bufferSize)
                audioLine.start()

                val bytes = ByteArray(bufferSize)
                while (isActive) {
                    val readLength = withContext(Dispatchers.IO) {
                        audioLine.read(bytes, 0, bytes.size)
                    }
                    if (readLength > 0) {
                        val buffer = Buffer()
                        buffer.write(bytes, 0, readLength)
                        bufferFlow.emit(buffer)
                    }
                }
            } finally {
                audioLine.stop()
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