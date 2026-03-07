package com.zxhhyj.atorm.recorder

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
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

@SuppressLint("MissingPermission")
class AndroidPlatformRecorder(
    override val sampleRate: Int,
    override val sampleSizeInBits: Int,
    override val channels: Int,
    override val bufferSize: Int,
) : Recorder {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val recordJob = Job()

    private val channelConfig =
        if (channels == 1) AudioFormat.CHANNEL_IN_MONO else AudioFormat.CHANNEL_IN_STEREO

    private val audioFormat = when (sampleSizeInBits) {
        8 -> AudioFormat.ENCODING_PCM_8BIT
        16 -> AudioFormat.ENCODING_PCM_16BIT
        else -> throw NotImplementedError()
    }

    private val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private val audioRecord by lazy {
        val actualBufferSize = maxOf(bufferSize, minBufferSize)
        AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            actualBufferSize
        )
    }

    private val bufferFlow = MutableSharedFlow<Buffer>()

    override val isAvailable: Boolean
        get() = audioRecord.state == AudioRecord.STATE_INITIALIZED

    override suspend fun collect(collector: FlowCollector<Source>) {
        bufferFlow.collect(collector)
    }

    override fun startRecording() {
        coroutineScope.launch(recordJob) {
            try {
                audioRecord.startRecording()

                val bytes = ByteArray(bufferSize)
                while (isActive) {
                    val readLength = withContext(Dispatchers.IO) {
                        audioRecord.read(bytes, 0, bytes.size)
                    }
                    if (readLength > 0) {
                        val buffer = Buffer()
                        buffer.write(bytes, 0, readLength)
                        bufferFlow.emit(buffer)
                    }
                }
            } finally {
                audioRecord.stop()
            }
        }
    }

    override fun stopRecording() {
        recordJob.cancelChildren()
    }

    override fun release() {
        coroutineScope.cancel()
        audioRecord.release()
    }
}
