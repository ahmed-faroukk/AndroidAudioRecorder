package com.ertaqy.recorder.features.uploading

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.ertaqy.recorder.RecordingViewModel
import com.ertaqy.recorder.base.animations.AFLoading
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

class UploadingScreen @Inject constructor(
    @ApplicationContext val context: Context,
    val file: File?,
    val recorder: AndroidAudioRecorder,
    val player: AndroidAudioPlayer,
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        val viewModel: RecordingViewModel = getViewModel()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .weight(3f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val isRecording = remember {
                    mutableStateOf(false)
                }

                RecordingUi(isRecording.value)

                Spacer(modifier = Modifier.height(25.dp))

                Button(onClick = {
                    val randomInt = Random.nextInt(50)
                    File(context.cacheDir, "Ertaqy_Call_record$randomInt.mp3").also {
                        recorder.start(it)
                        viewModel.setAudioFile(it)
                        viewModel.addToList(it)
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
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(3f)
                    .background(Color.DarkGray, RoundedCornerShape(25.dp))
                    .padding(25.dp),
            ) {
                item {
                    if (viewModel.files.isEmpty())
                        Column {
                            Text(text = "you have not recorde any audio yet ")
                        }
                }
                items(viewModel.files) { file ->
                    // FileItem(player,file)
                }
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
            color3 = Color.Red,
            circleSize = 10.dp
        )
    else
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
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
