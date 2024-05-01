package com.ertaqy.recorder.features.uploading

import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.ertaqy.recorder.base.animations.AFLoading
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import com.ertaqy.recorder.base.service.RecorderService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File

class UploadingScreen(@ApplicationContext val context: Context) : Screen {
    private val recorderService = RecorderService()
    private val recorder by lazy {
        AndroidAudioRecorder(context)
    }

    private val player by lazy {
        AndroidAudioPlayer(context)
    }

    private var audioFile: File? = null

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isRecording = remember {
                mutableStateOf(false)
            }

            RecordingUi(isRecording.value)
            Spacer(modifier = Modifier.height(25.dp))
            Button(onClick = {
                recorderService.startService(context)
                audioFile = recorderService.audioFile
                isRecording.value = true

            }) {
                Text(text = "Start recording")
            }
            Button(onClick = {
                recorder.stop()
                isRecording.value = false
            }) {
                Text(text = "Stop recording")
            }
            Button(onClick = {
                player.playFile( audioFile ?: return@Button)
            }) {
                Text(text = "Play")
            }
            Button(onClick = {
                player.stop()
            }) {
                Text(text = "Stop playing")
            }
        }
    }
}


@Composable
fun RecordingUi(isRecording : Boolean){
    if (isRecording)
        AFLoading(
            color1 = Color.Red,
            color2 = Color.Red,
            color3 = Color.Red
        ) else
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = Color.Red,
                    shape = CircleShape
                )
        )
}

@Composable
fun RecordBtn(){
    Box {
        Icon(imageVector = Icons.Filled.Send, contentDescription = "")
    }
}
