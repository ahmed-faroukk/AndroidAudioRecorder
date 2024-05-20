package com.ertaqy.recorder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import com.ertaqy.recorder.base.service.RecorderService
import com.ertaqy.recorder.base.service.ServiceActions
import com.ertaqy.recorder.features.uploading.RecordingUi
import com.ertaqy.recorder.features.uploading.RecordingViewModel
import com.ertaqy.recorder.features.uploading.composables.AudioPlayerBtn
import com.ertaqy.recorder.ui.theme.ErtaqyDeliveryCallRecorderTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.random.Random


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_PERMISSIONS = 1001

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: RecordingViewModel by viewModels<RecordingViewModel>()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO ,Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_PERMISSIONS
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
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val isPlaying = remember {
                        mutableStateOf(false)
                    }
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
                        AudioPlayerBtn(isPlaying = isPlaying, playOnClick = {
                            try {
                                val randomInt = Random.nextInt(50)
                                File(cacheDir, "Ertaqy_Call_record$randomInt.mp3").also {
                                    recorder.start(it)
                                    viewModel.setAudioFile(it)
                                    viewModel.addToList(it)
                                }
                                isRecording.value = true
                                //   startService(applicationContext)
                                Log.d("UploadingScreen", "Audio file is $file")
                            }catch (e : Exception){
                             println("e in start is" + e)
                            }

                        }, stopOnClick = {
                            try {
                                recorder.stop()
                                //   stopService(applicationContext)
                                isRecording.value = false
                                Log.d("UploadingScreen", "Audio file is $file")
                                player.stop()
                            }catch (e : Exception){
                                println("e in stop is " + e)
                            }

                        })

                    }

                    LazyColumn(
                        Modifier
                            .weight(4f)
                            .fillMaxSize()
                            .background(Color.DarkGray, RoundedCornerShape(25.dp))
                            .padding(25.dp),
                    ) {
                        item {
                            if (viewModel.files.isEmpty())
                                Column(
                                    Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "you have not been making any records yet",
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        style = TextStyle(fontSize = 12.sp)
                                    )
                                }
                        }
                        items(viewModel.files) { file ->
                            val time = viewModel.getFileTime(file.path, applicationContext)
                            FileItem(player, file, time, isPlaying, viewModel)
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
fun FileItem(
    player: AndroidAudioPlayer,
    file: File,
    time: String?,
    isPlaying: MutableState<Boolean>,
    viewModel: RecordingViewModel,
) {
    val scope = rememberCoroutineScope()

   val duration = time?.toLong()?.let { viewModel.formatDuration(it) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                player.playFile(file)
            }
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = Color.Black,
            ),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .shadow(25.dp)
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = file.name.toString(),
                    modifier = Modifier
                        .padding(15.dp),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = duration ?: "",
                    modifier = Modifier
                        .padding(10.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )

            }


        }

    }
    Spacer(modifier = Modifier.height(30.dp))
}

@Composable
fun TrackSlider(
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    songDuration: Float,
) {
    Slider(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        onValueChangeFinished = {

            onValueChangeFinished()

        },
        valueRange = 0f..songDuration,
        colors = SliderDefaults.colors(
            thumbColor = Color.Black,
            activeTrackColor = Color.DarkGray,
            inactiveTrackColor = Color.Gray,
        )
    )
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