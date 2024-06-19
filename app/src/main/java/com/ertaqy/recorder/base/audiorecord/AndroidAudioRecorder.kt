package com.ertaqy.recorder.base.audiorecord

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
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

    override fun start(file: File?) {
        if (file == null) {
            Log.e("Recorder", "Invalid file path")
            return
        }

        // Check permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Recorder", "Required permissions not granted")
            return
        }

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val mediaRecorder = createRecorder()
                val audioSource = MediaRecorder.AudioSource.MIC
                    mediaRecorder.apply {
                        setAudioSource(audioSource)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setOutputFile(FileOutputStream(file).fd)
                        prepare()
                        start()
                        recorder = this

                        Log.d(
                            "Recorder",
                            "Recording started with VOICE_DOWNLINK: ${file.absolutePath}"
                        )
                    }

            } catch (e: Exception) {
                Log.e("Recorder", "Failed to start recording", e)
                recorder?.release()
                recorder = null
            }

        }

    }
    // Check if the audio source is supported
    private fun isAudioSourceSupported(audioSource: Int): Boolean {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
            devices.any { it.type == audioSource }
        } catch (e: Exception) {
            Log.e("Recorder", "Error checking audio source support", e)
            false
        }
    }


    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}

/*
*
(deffacts f1
(lamp 1 off)
(lamp 2 on)
(lamp 3 off)
(lamp 4 on)
)

(def rule r1
?x <-(lamp ?c Off )
=>
(retract ?x )
)
# functoin
(
//deffundtion f1 (?n)
(printout t ?n crif)
* )


// fundtions
def
*
*
*
*
*
*
*
*
* */