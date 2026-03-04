package com.zxhhyj.atorm.recorder

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

class WindowsAudioRecorder : BaseAudioRecorder() {
    override fun initializeLineAndFormat(): Boolean =
        try {
            val format = AudioFormat(44100f, 16, 1, true, false)
            val info = DataLine.Info(TargetDataLine::class.java, format)
            val line = AudioSystem.getLine(info) as TargetDataLine
            this.line = line
            this.format = format
            true
        } catch (e: Exception) {
            WavRecorderEventHandler.INSTANCE?.failedToInitializeOnWindows(e)
            false
        }
}