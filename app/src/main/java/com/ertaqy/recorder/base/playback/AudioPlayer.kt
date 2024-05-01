package com.ertaqy.recorder.base.playback
import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}