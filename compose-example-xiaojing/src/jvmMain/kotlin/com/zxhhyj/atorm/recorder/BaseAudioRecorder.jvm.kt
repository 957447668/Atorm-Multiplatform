package com.zxhhyj.atorm.recorder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Buffer
import kotlinx.io.Source
import java.io.IOException
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.TargetDataLine

abstract class BaseAudioRecorder : AutoCloseable, Recorder {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    protected lateinit var line: TargetDataLine
    protected lateinit var format: AudioFormat

    private var _isAvailable = false
    override val isAvailable: Boolean
        get() = _isAvailable

    private val _buffer = Buffer()

    private val mutex = Mutex()

    override suspend fun <T> buffer(block: (Source) -> T): T {
        return mutex.withLock {
            block(_buffer)
        }
    }

    init {
        _isAvailable = initializeLineAndFormat()
    }

    protected abstract fun initializeLineAndFormat(): Boolean

    private val recordJob = Job()

    override fun startRecording() {
        coroutineScope.launch(recordJob) {
            if (!line.isOpen) {
                line.open(format)
            }
            line.start()
            try {
                val buffer = ByteArray(1024)
                while (true) {
                    val bytesRead = line.read(buffer, 0, buffer.size)
                    if (bytesRead > 0) {
                        mutex.withLock {
                            _buffer.write(buffer, 0, bytesRead)
                        }
                    }
                }
            } catch (e: IOException) {
                WavRecorderEventHandler.INSTANCE?.errorWhileRecording(e)
            } finally {
                line.close()
            }
        }
    }

    override fun stopRecording() {
        recordJob.cancelChildren()
    }

    override fun close() {
        line.close()
        coroutineScope.cancel()
    }
}