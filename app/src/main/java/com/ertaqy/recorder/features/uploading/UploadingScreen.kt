package com.ertaqy.recorder.features.uploading

import android.content.Context
import android.graphics.drawable.Icon
import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.ertaqy.recorder.base.animations.AFLoading
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import com.ertaqy.recorder.base.service.RecorderService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class UploadingScreen @Inject constructor(@ApplicationContext val context: Context) : Screen {

    @Composable
    override fun Content() {

        val viewModel: RecordingViewModel = getViewModel()
        val recorder by lazy {
            AndroidAudioRecorder(context)
        }
        val player by lazy {
            AndroidAudioPlayer(context)
        }
        Log.d("from Compose fun ", viewModel.audioFile.value.toString())
        val file = viewModel.audioFile.value
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
                File(context.cacheDir, "audio.mp3").also {
                    recorder.start(it)
                    viewModel.setAudioFile(it)

                }

                isRecording.value = true
                Log.d("UploadingScreen", "Audio file is $file")

            }) {
                Text(text = "Start recording")
            }

            Button(onClick = {

                recorder.stop()
                isRecording.value = false
                Log.d("UploadingScreen", "Audio file is $file")

            }) {
                Text(text = "Stop recording")
            }

            Button(onClick = {
                Log.d("UploadingScreen", "Audio file is $file")
                val file = viewModel.audioFile.value
                if (file?.exists() == true) {
                    player.playFile(file)
                } else {
                    Log.d("UploadingScreen", "Audio file doesn't exist")
                }
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
fun RecordingUi(isRecording: Boolean) {
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
fun RecordBtn() {
    Box {
        Icon(imageVector = Icons.Filled.Send, contentDescription = "")
    }
}
