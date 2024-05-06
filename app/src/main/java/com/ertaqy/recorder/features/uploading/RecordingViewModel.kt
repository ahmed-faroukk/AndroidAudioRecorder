package com.ertaqy.recorder.features.uploading

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.nio.file.Files
import java.util.Queue
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(

) : ViewModel() {

    private var _audioFile: MutableState<File?> = mutableStateOf(null)
    var audioFile = _audioFile

    private var _files = mutableListOf<Map<String , File>>()
    val files = _files

    fun addToList( filename : String , file : File) {
        val newRecord : Map<String , File> = mapOf(filename to file)
        _files.add(newRecord)
        println("new list state is ${_files.toString()}")
    }
    fun setAudioFile(file: File?) {
        _audioFile.value = file
    }
    init {
        Log.d("from ViewModel" ,  _audioFile.value.toString() )
    }



}