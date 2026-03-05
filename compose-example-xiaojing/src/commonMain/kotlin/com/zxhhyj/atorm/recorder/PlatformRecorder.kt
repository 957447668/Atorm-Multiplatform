package com.zxhhyj.atorm.recorder

expect class PlatformRecorder(
    sampleRate: Int,
    sampleSizeInBits: Int,
    channels: Int,
    bufferSize: Int
) : Recorder