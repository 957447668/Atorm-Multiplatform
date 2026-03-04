package com.zxhhyj.atorm

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.alibaba.dashscope.audio.asr.translation.TranslationRecognizerParam
import com.zxhhyj.atorm.recorder.platformRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.io.Buffer
import kotlin.time.Duration.Companion.seconds


@Composable
@Preview
fun App() {
    MaterialTheme {
        LaunchedEffect(Unit) {
            val targetLanguage = "en"

            val recognizerParam =
                TranslationRecognizerParam.builder() // 若没有将API Key配置到环境变量中，需将your-api-key替换为自己的API Key
                    .apiKey("your-api-key")
                    .model("gummy-realtime-v1") // 设置模型名
                    .format("pcm") // 设置待识别音频格式，支持的音频格式：pcm、wav、mp3、opus、speex、aac、amr
                    .sampleRate(16000) // 设置待识别音频采样率（单位Hz）。支持16000Hz及以上采样率。
                    .transcriptionEnabled(true) // 设置是否开启实时识别
                    .sourceLanguage("auto") // 设置源语言（待识别/翻译语言）代码
                    .translationEnabled(false) // 设置是否开启实时翻译
                    .translationLanguages(arrayOf(targetLanguage)) // 设置翻译目标语言
                    .build()

            val recorder = platformRecorder.apply {
                startRecording()
            }
            delay(1.seconds)
            val buffer = Buffer()
            val length: Long = 1024
            while (isActive) {
                val i = recorder.buffer {
                    it.readAtMostTo(buffer, length)
                }
                if (i > 0) {
                    println(i)
                }
            }
            println("end of recording")
        }
    }
}