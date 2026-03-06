package com.zxhhyj.atorm

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

private sealed interface AsrResult {
    class TempResult(val text: String) : AsrResult
    class Fix(val text: String) : AsrResult
}

@Composable
@Preview
fun App() {
    MaterialTheme {
//        LaunchedEffect(Unit) {
//            val targetLanguage = "en"
//
//            val recognizerParam =
//                TranslationRecognizerParam.builder()
//                    .apiKey("sk-b18e3ee8203b4ab89dd9fd2936c53454")
//                    .model("gummy-realtime-v1") // 设置模型名
//                    .format("pcm") // 设置待识别音频格式，支持的音频格式：pcm、wav、mp3、opus、speex、aac、amr
//                    .sampleRate(16000) // 设置待识别音频采样率（单位Hz）。支持16000Hz及以上采样率。
//                    .transcriptionEnabled(true) // 设置是否开启实时识别
//                    .sourceLanguage("zh") // 设置源语言（待识别/翻译语言）代码
//                    .translationEnabled(false) // 设置是否开启实时翻译
//                    .translationLanguages(arrayOf(targetLanguage)) // 设置翻译目标语言
//                    .build()
//
//            val callback = object : ResultCallback<TranslationRecognizerResult>() {
//                override fun onEvent(message: TranslationRecognizerResult) {
//                    if (message.transcriptionResult != null) {
//                        println("Transcription Result:$message")
//                        if (message.isSentenceEnd) {
//                            println("\tFix:" + message.transcriptionResult.getText())
//                        } else {
//                            println("\tTemp Result:" + message.transcriptionResult.getText())
//                        }
//                    }
//                }
//
//                override fun onComplete() {}
//
//                override fun onError(e: Exception?) {
//                    e?.printStackTrace()
//                }
//            }
//
//            val translator = TranslationRecognizerRealtime()
//
//            translator.call(recognizerParam, callback)
//
//            val frameTime = 20
//            val sampleRate = 16000
//            val bufferSize = sampleRate / frameTime
//
//            val platformRecorder = PlatformRecorder(sampleRate, 16, 1, bufferSize)
//            platformRecorder.startRecording()
//            val buffer = ByteBuffer.allocate(bufferSize)
//            try {
//                println("请您通过麦克风讲话体验实时语音识别和翻译功能")
//                platformRecorder.collect {
//                    it.readTo(buffer.array())
//                    translator.sendAudioFrame(buffer)
//                    buffer.clear()
//                }
//            } finally {
//                translator.stop()
//            }
//        }
        Box(Modifier.fillMaxSize()) {
            val backgroundColor = Color.White
            val backdrop = rememberLayerBackdrop {
                drawRect(backgroundColor)
                drawContent()
            }

            LazyColumn(
                modifier = Modifier.layerBackdrop(backdrop)
            ) {
                item {
                    Box(modifier = Modifier.aspectRatio(1f).background(Color.Red))
                }
                item {
                    Box(modifier = Modifier.aspectRatio(1f).background(Color.Blue))
                }
                item {
                    Box(modifier = Modifier.aspectRatio(1f).background(Color.Gray))
                }
            }

            Row(
                modifier = Modifier
                    .safeContentPadding()
                    .height(64f.dp)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(8f.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var recordButtonPressState by remember { mutableStateOf(false) }
                val recordButtonScale by animateFloatAsState(if (recordButtonPressState) 0.98f else 1f)
                Box(
                    modifier = Modifier
                        .scale(recordButtonScale)
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { CircleShape },
                            effects = {
                                vibrancy()
                                blur(4f.dp.toPx())
                                lens(16f.dp.toPx(), 32f.dp.toPx())
                            },
                            onDrawSurface = { drawRect(Color.White.copy(alpha = 0.5f)) }
                        )
                        .fillMaxHeight()
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    recordButtonPressState = true
                                    tryAwaitRelease()
                                    recordButtonPressState = false
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("按住说话")
                }
                Box(
                    Modifier
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { CircleShape },
                            effects = {
                                vibrancy()
                                blur(4f.dp.toPx())
                                lens(16f.dp.toPx(), 32f.dp.toPx())
                            },
                            onDrawSurface = {
                                val tint = Color(0xFF0088FF)
                                drawRect(tint, blendMode = BlendMode.Hue)
                                drawRect(tint.copy(alpha = 0.75f))
                            }
                        )
                        .aspectRatio(1f)
                )
            }
        }
    }
}