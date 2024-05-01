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
import androidx.core.app.NotificationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ertaqy.recorder.R
import com.ertaqy.recorder.base.audiorecord.AndroidAudioRecorder
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import java.io.File

enum class ServiceActions {
    START, STOP
}
class RecorderService : Service() {

    // try shared viewmodel

    private val recorder by lazy {
        AndroidAudioRecorder(this)
    }

    private val player by lazy {
        AndroidAudioPlayer(this)
    }

     var audioFile: File? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() called")
        when (intent?.action) {
            ServiceActions.START.toString() -> startForegroundService()
            ServiceActions.STOP.toString() -> stopForegroundService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called")
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
            File(this.cacheDir, "audio.mp3").also {
                recorder.start(it)
                audioFile = it
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error starting foreground service: ${e.message}")
        }
    }

    private fun stopForegroundService() {
        stopSelf()
        recorder.stop()
    }

    fun stopService(context : Context){
        val intent = Intent(context, RecorderService::class.java).apply {
            action = ServiceActions.STOP.toString()
        }
        context.startService(intent)
    }

    fun startService(context : Context){
        val intent = Intent(context, RecorderService::class.java).apply {
            action = ServiceActions.START.toString()
        }
        context.startService(intent)
    }


    companion object {
        private const val TAG = "ForegroundService"
        private const val NOTIFICATION_CHANNEL_ID = "Recorder"
        private const val NOTIFICATION_CHANNEL_NAME = "running_notifications"
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }
}