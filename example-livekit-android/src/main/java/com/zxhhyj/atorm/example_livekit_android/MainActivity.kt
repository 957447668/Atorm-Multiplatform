package com.zxhhyj.atorm.example_livekit_android

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.zxhhyj.atorm.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FeatureThatRequiresRecordPermission(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                App()
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun FeatureThatRequiresRecordPermission(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {

        val recordPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

        if (recordPermissionState.status.isGranted) {
            content()
        } else {
            Column(modifier = modifier) {
                val textToShow = if (recordPermissionState.status.shouldShowRationale) {
                    "The microphone is important for this app. Please grant the permission."
                } else {
                    "Record audio permission required for this feature to be available. " +
                            "Please grant the permission"
                }
                Text(textToShow)
                Button(onClick = { recordPermissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}