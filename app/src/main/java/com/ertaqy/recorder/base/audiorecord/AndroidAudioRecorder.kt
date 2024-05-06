package com.ertaqy.recorder.base.audiorecord

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.ertaqy.recorder.features.uploading.RecordingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AndroidAudioRecorder @Inject constructor(
    private val context: Context ,
): AudioRecorder {

    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start(file : File?) {
        CoroutineScope(Dispatchers.Default).launch {
            createRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(FileOutputStream(file).fd)
                prepare()
                start()
                recorder = this
            }
        }
        Log.d("from recorder start_fun" ,  file.toString() )

    }

    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}