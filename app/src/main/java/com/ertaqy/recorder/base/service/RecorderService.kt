package com.ertaqy.recorder.base.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.ertaqy.recorder.R
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.random.Random

enum class ServiceActions {
    START, STOP
}

@AndroidEntryPoint
class RecorderService() : Service() {

    private var _audioFile: MutableState<File?> = mutableStateOf(null)
    var audioFile = _audioFile

    private var _files = mutableListOf<File>()
    val files = _files

    val recorder by lazy {
        AndroidAudioRecorder(this)
    }

    val isRecording by lazy {
        mutableStateOf(false)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() called")
        when (intent?.action) {
            ServiceActions.START.toString() -> startForegroundService()
            ServiceActions.STOP.toString() -> stopRecord()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()


    }

    private fun startRecord(){
        startForegroundService()
        val randomInt = Random.nextInt(50)
        File(cacheDir, "Ertaqy_Call_record$randomInt.mp3").also {
            recorder.start(it)
            setAudioFile(it)
            addToList(it)
        }
        isRecording.value = true
        com.ertaqy.recorder.startService(applicationContext)
        Log.d("UploadingScreen", "Audio file is $audioFile")
    }

    private fun stopRecord() {
        stopForegroundService()
        recorder.stop()
        com.ertaqy.recorder.stopService(applicationContext)
        isRecording.value = false
        Log.d("UploadingScreen", "Audio file is $audioFile")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind() called")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_fiber_manual_record_24)
            .setContentTitle("Ertaqy")
            .setContentText("Recording...")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        try {
            startForeground(FOREGROUND_NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting foreground service: ${e.message}")
        }
    }

    private fun stopForegroundService() {
        stopSelf()
    }
    fun addToList(file : File) {
        _files.add(file)
        println("new list state is ${_files.toString()}")
    }

    fun setAudioFile(file: File?) {
        _audioFile.value = file
    }


    companion object {
        private const val TAG = "ForegroundService"
        private const val NOTIFICATION_CHANNEL_ID = "Recorder"
        private const val NOTIFICATION_CHANNEL_NAME = "running_notifications"
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }
}