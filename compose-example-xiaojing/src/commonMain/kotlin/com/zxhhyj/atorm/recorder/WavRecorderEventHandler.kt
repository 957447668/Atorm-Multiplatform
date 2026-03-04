package com.zxhhyj.atorm.recorder


interface WavRecorderEventHandler {
    companion object {
        var INSTANCE: WavRecorderEventHandler? = null
    }

    fun failedToInitializeOnWindows(e: Exception) {}

    fun errorWhileRecording(e: Exception) {}
}