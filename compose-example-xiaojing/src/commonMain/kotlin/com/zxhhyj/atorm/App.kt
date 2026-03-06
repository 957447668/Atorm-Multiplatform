package com.zxhhyj.atorm

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.alibaba.dashscope.audio.asr.translation.TranslationRecognizerParam
import com.alibaba.dashscope.audio.asr.translation.TranslationRecognizerRealtime
import com.alibaba.dashscope.audio.asr.translation.results.TranslationRecognizerResult
import com.alibaba.dashscope.common.ResultCallback
import com.zxhhyj.atorm.recorder.PlatformRecorder
import kotlinx.io.readTo
import java.nio.ByteBuffer


@Composable
@Preview
fun App() {
    MaterialTheme {
        LaunchedEffect(Unit) {
            println("666")
            val targetLanguage = "en"

            val recognizerParam =
                TranslationRecognizerParam.builder() // 若没有将API Key配置到环境变量中，需将your-api-key替换为自己的API Key
                    .apiKey("")
                    .model("gummy-realtime-v1") // 设置模型名
                    .format("pcm") // 设置待识别音频格式，支持的音频格式：pcm、wav、mp3、opus、speex、aac、amr
                    .sampleRate(16000) // 设置待识别音频采样率（单位Hz）。支持16000Hz及以上采样率。
                    .transcriptionEnabled(true) // 设置是否开启实时识别
                    .sourceLanguage("zh") // 设置源语言（待识别/翻译语言）代码
                    .translationEnabled(false) // 设置是否开启实时翻译
                    .translationLanguages(arrayOf(targetLanguage)) // 设置翻译目标语言
                    .build()

            val callback = object : ResultCallback<TranslationRecognizerResult>() {
                override fun onEvent(message: TranslationRecognizerResult) {
                    if (message.transcriptionResult != null) {
                        println("Transcription Result:$message");
                        if (message.isSentenceEnd) {
                            println("\tFix:" + message.transcriptionResult.getText());
                        } else {
                            println("\tTemp Result:" + message.transcriptionResult.getText());
                        }
                    }
                }

                override fun onComplete() {

                }

                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                }
            }

            val translator = TranslationRecognizerRealtime()

            translator.call(recognizerParam, callback)

            val frameTime = 20
            val sampleRate = 16000
            val bufferSize = sampleRate / frameTime

            val platformRecorder = PlatformRecorder(sampleRate, 16, 1, bufferSize)
            platformRecorder.startRecording()
            val buffer = ByteBuffer.allocate(bufferSize)
            try {
                println("请您通过麦克风讲话体验实时语音识别和翻译功能")
                platformRecorder.collect {
                    it.readTo(buffer.array())
                    translator.sendAudioFrame(buffer)
                    buffer.clear()
                }
            } finally {
                translator.stop()
            }
        }
    }
}