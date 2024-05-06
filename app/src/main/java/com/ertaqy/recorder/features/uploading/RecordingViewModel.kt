package com.ertaqy.recorder.features.uploading

import android.media.MediaPlayer
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
    fun setAudioFile(file: File?) {
        _audioFile.value = file
    }
    init {
        Log.d("from ViewModel" ,  _audioFile.value.toString() )
    }



}