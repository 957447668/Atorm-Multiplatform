package com.zxhhyj.atorm.recorder

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

class MacOsAudioRecorder : BaseAudioRecorder() {
    override fun initializeLineAndFormat(): Boolean {
        val mixerInfos = AudioSystem.getMixerInfo()
        for (mixerInfo in mixerInfos) {
            val mixer = AudioSystem.getMixer(mixerInfo)
            val lineInfos = mixer.targetLineInfo
            for (lineInfo in lineInfos) {
                if (lineInfo is DataLine.Info) {
                    val line = mixer.getLine(lineInfo) as? TargetDataLine
                    val formats = lineInfo.formats
                    val format = formats.firstOrNull { it.sampleRate.toInt() != AudioSystem.NOT_SPECIFIED }
                    if (line != null && format != null) {
                        this.line = line
                        this.format = format
                        return true
                    }
                }
            }
        }
        return false
    }
}