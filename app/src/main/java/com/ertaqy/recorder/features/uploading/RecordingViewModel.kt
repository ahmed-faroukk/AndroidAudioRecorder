package com.ertaqy.recorder.features.uploading

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject


@HiltViewModel
class RecordingViewModel @Inject constructor(

) : ViewModel() {

    private var _audioFile: MutableState<File?> = mutableStateOf(null)
    var audioFile = _audioFile

    private var _files = mutableListOf<File>()
    val files = _files

    fun addToList(file : File) {
        _files.add(file)
        println("new list state is ${_files.toString()}")
    }
    fun setAudioFile(file: File?) {
        _audioFile.value = file
    }
    init {
        Log.d("from ViewModel" ,  _audioFile.value.toString() )
    }

    fun getFileTime(filePath: String, context: Context): String? {
        val uri = Uri.parse(filePath)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    }
    fun formatDuration(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


}