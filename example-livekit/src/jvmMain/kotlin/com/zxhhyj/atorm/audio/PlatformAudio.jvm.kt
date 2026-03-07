package com.zxhhyj.atorm.audio

actual class PlatformAudio actual constructor(
    sampleRate: Int,
    sampleSizeInBits: Int,
    channels: Int
) : Audio by JvmAudio(sampleRate, sampleSizeInBits, channels)