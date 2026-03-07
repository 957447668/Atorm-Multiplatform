package com.zxhhyj.atorm.audio

sealed interface AudioState {
    object NONE : AudioState
    object READY : AudioState
    object PLAYING : AudioState
    object PAUSED : AudioState
    class ERROR(val message: String) : AudioState
}