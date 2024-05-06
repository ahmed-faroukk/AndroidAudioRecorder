package com.ertaqy.recorder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import com.ertaqy.recorder.base.service.RecorderService
import com.ertaqy.recorder.base.service.ServiceActions
import com.ertaqy.recorder.features.uploading.RecordingUi
import com.ertaqy.recorder.features.uploading.RecordingViewModel
import com.ertaqy.recorder.ui.theme.ErtaqyDeliveryCallRecorderTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.random.Random


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: RecordingViewModel by viewModels<RecordingViewModel>()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        val recorder by lazy {
            AndroidAudioRecorder(this)
        }

        val player by lazy {
            AndroidAudioPlayer(this)
        }

        val file = viewModel.audioFile.value
        Log.d("from Compose fun ", viewModel.audioFile.value.toString())
        setContent {
            ErtaqyDeliveryCallRecorderTheme {
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
                        val randomInt = Random.nextInt(50)
                        File(cacheDir, "Ertaqy_Call_record$randomInt.mp3").also {
                            recorder.start(it)
                            viewModel.setAudioFile(it)
                            viewModel.addToList(it.name , it)
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
                    LazyColumn(Modifier.padding(25.dp)) {
                        items(viewModel.files){ file ->
                            Text(text = file.keys.toString() , Modifier.fillMaxWidth().background(
                                Color.White)
                            )
                        }
                    }
                }
                // A surface container using the 'background' color from the theme
                /*  Navigator(screen = SplashScreen()){
                      FadeTransition(navigator = it)
                  }*/
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ErtaqyDeliveryCallRecorderTheme {
        Greeting("Android")
    }
}

fun stopService(context: Context) {
    val intent = Intent(context, RecorderService::class.java).apply {
        action = ServiceActions.STOP.toString()
    }
    context.startService(intent)
}

fun startService(context: Context) {
    val intent = Intent(context, RecorderService::class.java).apply {
        action = ServiceActions.START.toString()
    }
    context.startService(intent)
}