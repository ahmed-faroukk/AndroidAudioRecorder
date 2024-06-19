package com.ammar.callrecording.RecordingService

import android.content.Context
import android.media.AudioManager
import android.media.MediaRecorder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import java.io.File
import java.util.Date

class CallStateListener(private val context: Context) : PhoneStateListener() {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var filePath = ""
    private val androidAudioPlayer = AndroidAudioPlayer(context)


    @Deprecated("Deprecated in Java")
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        when (state) {
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                startRecording()
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                stopRecording()
            }
            TelephonyManager.CALL_STATE_RINGING -> {
                Log.d("recording" , "CALL_STATE_RINGING")
            }
        }
    }


    private fun startRecording() {
        if (!isRecording) {
            try {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.requestAudioFocus(
                    null,
                    AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )
                mediaRecorder = MediaRecorder()
                mediaRecorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                    setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    setAudioSamplingRate(44100);
                    filePath = context.getExternalFilesDir(null)?.absolutePath + "/recorded_call_${Date().time}.mp3"
                    setOutputFile(filePath)
                    prepare()
                    start()
                    isRecording = true
                    Log.d("recording" , "starting....")
                }
            } catch (e: Exception) {
//                e.printStackTrace()
                Log.d("recording", "err => $e")
            }
        }
    }


    private fun stopRecording() {
        if (isRecording) {
            try {
                mediaRecorder?.apply {
                    stop()
                    reset()
                    release()
                    isRecording = false
                    val file = File(context.filesDir, filePath)
                    Log.d("recording" , "Done")
                }

                if (filePath.isNotEmpty()) {
                    startPlay(filePath)
                    println("playing now ")
                }

                else Log.d("recording" , "file is empty")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startPlay(filepath : String) {
        val audioFile = File(filePath)
        try {
            androidAudioPlayer.playFile(audioFile)
        }catch (e : Throwable){
            Log.d("error while start playing audio " , e.message.toString())
        }
    }

}
