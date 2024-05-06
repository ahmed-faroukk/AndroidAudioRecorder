package com.ertaqy.recorder.base.audiorecord

import java.io.File

interface AudioRecorder {
    fun start(file: File?)
    fun stop()
}