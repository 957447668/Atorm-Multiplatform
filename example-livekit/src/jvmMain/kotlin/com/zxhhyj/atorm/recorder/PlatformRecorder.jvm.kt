package com.zxhhyj.atorm.recorder

actual class PlatformRecorder actual constructor(
    sampleRate: Int,
    sampleSizeInBits: Int,
    channels: Int,
    bufferSize: Int
) : Recorder by JvmPlatformRecorder(sampleRate, sampleSizeInBits, channels, bufferSize)