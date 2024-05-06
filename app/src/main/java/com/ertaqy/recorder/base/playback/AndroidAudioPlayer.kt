package com.ertaqy.recorder.base.playback
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.ertaqy.recorder.features.uploading.RecordingViewModel
import java.io.File
import javax.inject.Inject

class AndroidAudioPlayer @Inject constructor(
    private val context : Context ,
) : AudioPlayer {

    private var player: MediaPlayer? = null
    override fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            try {
                start()
            }catch (e : Exception){
                Log.d("start tracking errors " , e.message.toString())
            }
        }
    }
    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

}