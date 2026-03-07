package com.zxhhyj.atorm.audio

expect class PlatformAudio(
    sampleRate: Int,
    sampleSizeInBits: Int,
    channels: Int,
) : Audio