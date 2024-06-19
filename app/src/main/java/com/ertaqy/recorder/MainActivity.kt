package com.ertaqy.recorder

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import coil.compose.rememberImagePainter
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import com.ertaqy.recorder.base.service.RecorderService
import com.ertaqy.recorder.base.service.ServiceActions
import com.ertaqy.recorder.features.uploading.RecordingUi
import com.ertaqy.recorder.features.uploading.composables.AudioPlayerBtn
import com.ertaqy.recorder.ui.theme.ErtaqyDeliveryCallRecorderTheme
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.comparator.LastModifiedFileComparator
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.filefilter.WildcardFileFilter
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileFilter
import java.util.Arrays
import kotlin.random.Random


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val REQUEST_CODE_PERMISSIONS = 1001
    private val pickDirectory = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                // Handle the selected directory URI here
                val path = it.path
                // Perform operations with the selected path
                // For example, display the selected path
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("activityAF" , "start onStart")

    }

    override fun onPause() {
        super.onPause()
        Log.d("activityAF" , "start onPause")


    }

    override fun onStop() {
        super.onStop()
        Log.d("activityAF" , "start onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.d("activityAF" , "start onResume")

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

                    // ShowLastImage()
                    GetLastAudio()
                    FileUploadComponent{

                    }
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
    fun openFolder(location: String, context: Context) {
        // location = "/sdcard/my_folder";
        val intent = Intent(Intent.ACTION_VIEW)
        val mydir = Uri.parse("file://$location")
        intent.setDataAndType(mydir, "*/*") // or use "application/*" if you want to specify the application type
        context.startActivity(intent)
    }
    private fun openDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.putExtra(Intent.EXTRA_TITLE, "Select Directory")
        pickDirectory.launch(intent)
    }


}
@Composable
fun FileUploadComponent(onFileSelected: (uri: Uri) -> Unit) {
    val context = LocalContext.current
    val contentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Handle the returned URI here
        uri?.let { onFileSelected(it) }
    }
    // get last call


    Column {
        Button(onClick = {
           // contentLauncher.launch("Music/Recordings/Call Recordings/*")
            val callRecordingsDir = File("Music/*")
            val lastCall = getLastCallInCallRecordings(callRecordingsDir)
            if (lastCall != null) {
                println(lastCall.name)
            } else {
                println("No calls found in the directory")
            }
        }) {
            // Change "image/*" to specify the type of file you want to allow the user to pick
            Text("Pick a file")
        }
    }
}
fun getLastCallInCallRecordings(callRecordingsDir: File): File? {
    val calls = callRecordingsDir.listFiles()
    return calls?.lastOrNull()
}

fun getTheNewestFile(filePath: String?, ext: String): File? {
    var theNewestFile: File? = null
    val dir = File(filePath)
    val fileFilter: FileFilter = WildcardFileFilter("*.$ext")
    val files = dir.listFiles(fileFilter)

    if (files.size > 0) {
        /** The newest file comes first  */
        Arrays.sort<File>(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE)
        theNewestFile = files[0]
    }

    return theNewestFile
}
@Composable
fun ShowLastImage() {
    val context = LocalContext.current
    var imageUri = remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.MIME_TYPE
        )

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))
                val imageUriTemp = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageUri.value = imageUriTemp
            }
        }
    }

    Image(
        painter = rememberImagePainter(imageUri.value),
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
    ) ?: Text("No image found", modifier = Modifier.fillMaxSize())
}

@Composable
fun GetLastAudio() {
    val context = LocalContext.current
    var audioUri = remember { mutableStateOf<Uri?>(null) }
    val mediaPlayer = remember { MediaPlayer() }

    LaunchedEffect(Unit) {
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.DATE_ADDED
        )

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Audio.AudioColumns.DATE_ADDED} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID))
                val audioUriTemp = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                audioUri.value = audioUriTemp
            }
        }
    }

    audioUri.value.let { uri ->
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Playing the latest audio file")
            Button(onClick = {
                mediaPlayer.reset()
                uri?.let { mediaPlayer.setDataSource(context, it) }
                mediaPlayer.prepare()
                mediaPlayer.start()
            }) {
                Text("Play Audio")
            }
            DisposableEffect(Unit) {
                onDispose {
                    mediaPlayer.release()
                }
            }
        }
    } ?: Text("No audio file found", modifier = Modifier.fillMaxSize())
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
                    text = duration ?: "Recording....",
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
